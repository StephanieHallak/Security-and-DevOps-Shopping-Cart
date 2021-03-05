package com.example.demo.controllersTests;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRep = mock(ItemRepository.class);

    private List<Item> createListItems(){
        List<Item> itemList = new ArrayList<>();
        for (long i = 0L; i < 4L; i++){
            Item item = new Item();
            item.setPrice(BigDecimal.valueOf(3.5));
            item.setDescription("ItemTest");
            item.setName("ItemTest");
            item.setId(i+1L);
            itemList.add(item);
        }
        return itemList;
    }

    @Before
    public void setup(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRep);
    }

    @Test
    public void test_get_all_items(){
        List<Item> expectedItems = createListItems();
        when(itemRep.findAll()).thenReturn(expectedItems);
        final ResponseEntity<List<Item>> response = itemController.getItems();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        List<Item> actualItems = response.getBody();
        Assert.assertNotNull(actualItems);
        Assert.assertEquals(expectedItems.size(), actualItems.size());
        Assert.assertEquals(expectedItems.get(0).getId(), actualItems.get(0).getId());
    }

    @Test
    public void  test_get_item_by_id(){
        List<Item> expectedItems = createListItems();
        long index = 0;
        when(itemRep.findById(index)).thenReturn(java.util.Optional.ofNullable(expectedItems.get((int) index)));
        final ResponseEntity<Item> response = itemController.getItemById(index);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Item actualItem = response.getBody();
        Assert.assertEquals(expectedItems.get((int) index).getName(), actualItem.getName());
    }

    @Test
    public void get_item_by_name(){
        List<Item> expectedItems = createListItems();
        String name = "ItemTest";
        when(itemRep.findByName(name)).thenReturn(expectedItems);
        final ResponseEntity<List<Item>> response = itemController.getItemsByName(name);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        List<Item> actualItems = response.getBody();
        Assert.assertEquals(expectedItems, actualItems);
        Assert.assertEquals(expectedItems.size(), actualItems.size());

    }
}
