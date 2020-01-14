package io.github.madushanka.pos.business.custom.impl;


import io.github.madushanka.pos.business.custom.OrderBO;
import io.github.madushanka.pos.dao.DAOFactory;
import io.github.madushanka.pos.dao.DAOTypes;
import io.github.madushanka.pos.dao.custom.ItemDAO;
import io.github.madushanka.pos.dao.custom.OrderDAO;
import io.github.madushanka.pos.dao.custom.OrderDetailDAO;
import io.github.madushanka.pos.dao.custom.QueryDAO;
import io.github.madushanka.pos.db.DBConnection;
import io.github.madushanka.pos.dto.OrderDTO;
import io.github.madushanka.pos.dto.OrderDTO2;
import io.github.madushanka.pos.dto.OrderDetailDTO;
import io.github.madushanka.pos.entity.CustomEntity;
import io.github.madushanka.pos.entity.Item;
import io.github.madushanka.pos.entity.Order;
import io.github.madushanka.pos.entity.OrderDetail;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderBOImpl implements OrderBO {

    private OrderDAO orderDAO = DAOFactory.getInstance().getDAO(DAOTypes.ORDER);
    private OrderDetailDAO orderDetailDAO = DAOFactory.getInstance().getDAO(DAOTypes.ORDER_DETAIL);
    private ItemDAO itemDAO = DAOFactory.getInstance().getDAO(DAOTypes.ITEM);
    private QueryDAO queryDAO = DAOFactory.getInstance().getDAO(DAOTypes.QUERY);

    @Override
    public int getLastOrderId() throws Exception {
        return orderDAO.getLastOrderId();
    }

    @Override
    public boolean placeOrder(OrderDTO order) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {

            // Let's start a transaction
            connection.setAutoCommit(false);

            int oId = order.getId();
            boolean result = orderDAO.save(new Order(oId, new java.sql.Date(new Date().getTime()),
                    order.getCustomerId()));

            if (!result) {
                connection.rollback();
                throw new RuntimeException("Something, something went wrong");
            }

            for (OrderDetailDTO orderDetail : order.getOrderDetails()) {
                result = orderDetailDAO.save(new OrderDetail(oId, orderDetail.getCode(),
                        orderDetail.getQty(), orderDetail.getUnitPrice()));

                if (!result) {
                    connection.rollback();
                    throw new RuntimeException("Something, something went wrong");
                }

                Item item = itemDAO.find(orderDetail.getCode());
                item.setQtyOnHand(item.getQtyOnHand() - orderDetail.getQty());
                result = itemDAO.update(item);

                if (!result) {
                    connection.rollback();
                    throw new RuntimeException("Something, something went wrong");
                }
            }

            connection.commit();
            return true;

        } catch (Throwable e) {

            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<OrderDTO2> getOrderInfo(String query) throws Exception {
        List<CustomEntity> ordersInfo = queryDAO.getOrdersInfo(query + "%");
        List<OrderDTO2> dtos = new ArrayList<>();
        for (CustomEntity info : ordersInfo) {
            dtos.add(new OrderDTO2(info.getOrderId(),
                    info.getOrderDate(),info.getCustomerId(),info.getCustomerName(),info.getOrderTotal()));
        }
        return dtos;
    }
}
