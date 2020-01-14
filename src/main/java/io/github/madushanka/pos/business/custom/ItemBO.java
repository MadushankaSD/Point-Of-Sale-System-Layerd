package io.github.madushanka.pos.business.custom;


import io.github.madushanka.pos.business.SuperBO;
import io.github.madushanka.pos.dto.ItemDTO;

import java.util.List;

public interface ItemBO extends SuperBO {

    boolean saveItem(ItemDTO itemDTO) throws Exception;

    boolean updateItem(ItemDTO item) throws Exception;

    boolean deleteItem(String itemCode) throws Exception;

    List<ItemDTO> findAllItems() throws Exception;

    String getLastItemCode() throws Exception;

    ItemDTO findItem(String itemCode) throws Exception;

    List<String> getAllItemCodes() throws Exception;

}
