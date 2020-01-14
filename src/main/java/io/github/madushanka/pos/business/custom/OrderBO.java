package io.github.madushanka.pos.business.custom;


import io.github.madushanka.pos.business.SuperBO;
import io.github.madushanka.pos.dto.OrderDTO;
import io.github.madushanka.pos.dto.OrderDTO2;

import java.util.List;

public interface OrderBO extends SuperBO {

    int getLastOrderId() throws Exception;

    boolean placeOrder(OrderDTO orderDTO) throws Exception;

    List<OrderDTO2> getOrderInfo(String query) throws Exception;

}
