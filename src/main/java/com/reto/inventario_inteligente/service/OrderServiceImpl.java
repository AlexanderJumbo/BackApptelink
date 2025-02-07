package com.reto.inventario_inteligente.service;

import com.reto.inventario_inteligente.dto.ControlTableRequest;
import com.reto.inventario_inteligente.dto.OrderDetailResponse;
import com.reto.inventario_inteligente.dto.OrderResponse;
import com.reto.inventario_inteligente.entity.ControlTable;
import com.reto.inventario_inteligente.entity.Order;
import com.reto.inventario_inteligente.entity.OrderDetail;
import com.reto.inventario_inteligente.entity.Product;
import com.reto.inventario_inteligente.entity.security.User;
import com.reto.inventario_inteligente.exception.ObjectNotFoundException;
import com.reto.inventario_inteligente.exception.RequestException;
import com.reto.inventario_inteligente.repository.ControlTableRepository;
import com.reto.inventario_inteligente.repository.OrderDetailRepository;
import com.reto.inventario_inteligente.repository.OrderRepository;
import com.reto.inventario_inteligente.repository.ProductRepository;
import com.reto.inventario_inteligente.repository.security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository detailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ControlTableService controlTableService;

    @Override
    public OrderResponse createOrder(Long clientId, List<OrderDetail> orderDetails) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ObjectNotFoundException("Cliente no encontrado"));

        Order newOrder = new Order();
        newOrder.setClient(client);
        newOrder.setOrderDate(LocalDate.now());

        double totalIva = 0.0;
        double subtotal = 0.0;
        double total = 0.0;
        // AGREGAR PRODUCTOS AL PEDIDO
        for (OrderDetail details: orderDetails){
            Product product = productRepository.findById(details.getProduct().getId())
                    .orElseThrow(() -> new ObjectNotFoundException("Product no found: " + details.getProduct().getId()));
            if(details.getQuantity() == 0){
                throw new RequestException(500, "La cantidad del producto: " + product.getName() + " debe ser al menos de 1");
            }
            if(details.getQuantity() > product.getStockQuantity()){
                throw new RequestException(500, "Stock no disponible, solo tenemos: " + product.getStockQuantity() + " de " + product.getName());
            }

            subtotal += (product.getPrice() * details.getQuantity());
            // Descontar del stock la cantidad pedida
            product.setStockQuantity(product.getStockQuantity() - details.getQuantity());
            productRepository.save(product);

            details.setProduct(product);
            details.setOrder(newOrder);
            details.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
            details.setSubtotal(product.getPrice() * details.getQuantity());
            // TABLA DE CONTROL
            ControlTableRequest controlRequest = new ControlTableRequest("PRODUCTO", "VENTA", product.getId(),
                    details.getQuantity(), BigDecimal.valueOf(product.getPrice()), BigDecimal.valueOf(product.getPrice() * details.getQuantity()), "VENTA DE PRODUCTO");
            ControlTable newMovement =  controlTableService.registerMovement(controlRequest);
        }

        totalIva = subtotal * 0.15;
        total = subtotal + totalIva;

        // GUARDAR PEDIDO
        newOrder.setTotal(BigDecimal.valueOf(total));
        newOrder.setSubtotal(BigDecimal.valueOf(subtotal));
        newOrder.setOrderDetails(orderDetails);
        return mapDTO(orderRepository.save(newOrder));
    }

    @Override // ELIMINARCIÓN LÓGICA DEL PEDIDO
    public Boolean changeOrderStatus(Long orderId) {
        Order orderFound = orderRepository.findById(orderId)
                .orElseThrow(() -> new ObjectNotFoundException("Order no found: " + orderId));
        orderFound.setStatus(!orderFound.getStatus());
        return orderRepository.save(orderFound).getStatus();
    }

    @Override
    public Page<OrderResponse> getAllOrders(int numberPage, int numberRecordsPerPage, String orderRecordsByField, String orderRecordsByDirection) {
        Sort sort = orderRecordsByDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderRecordsByField).ascending() : Sort.by(orderRecordsByField).descending();
        Pageable pageable = PageRequest.of(numberPage, numberRecordsPerPage, sort);

        return orderRepository.getAllOrders(pageable).map(order -> { //findAll(pageable).map( order -> {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(order.getId());
            orderResponse.setDateInvoice(order.getOrderDate().toString());
            orderResponse.setClientId(order.getClient().getId());
            orderResponse.setClient(order.getClient().getFirstname() + " " + order.getClient().getLastname());
            orderResponse.setAmount(Double.parseDouble(order.getTotal().toString()));
            return orderResponse;
        });
    }

    private OrderResponse mapDTO(Order newOrder) {

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(newOrder.getId());
        orderResponse.setDateInvoice(newOrder.getOrderDate().toString());
        orderResponse.setAmount(Double.parseDouble(newOrder.getTotal().toString()));
        orderResponse.setSubtotal(Double.parseDouble(newOrder.getSubtotal().toString()));
        orderResponse.setClientId(newOrder.getClient().getId());
        orderResponse.setClient(newOrder.getClient().getFirstname() + " " + newOrder.getClient().getLastname());
        orderResponse.setOrderDetails(newOrder.getOrderDetails().stream().map(orderDetail -> {
            OrderDetailResponse o = new OrderDetailResponse();
            o.setIdOrderDetail(orderDetail.getId());
            o.setUnits(orderDetail.getQuantity());
            o.setUnitPrice(orderDetail.getUnitPrice());
            o.setSubtotal(orderDetail.getSubtotal());
            o.setOrder(orderDetail.getOrder().getId());
            o.setProduct(orderDetail.getProduct().getId());
            o.setProductName(orderDetail.getProduct().getName());
            return o;
        }).collect(Collectors.toList()));
        return orderResponse;
    }
}
