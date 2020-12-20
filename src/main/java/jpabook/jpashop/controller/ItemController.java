package jpabook.jpashop.controller;


import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {

        Book book = new Book(); // 이거보다는 createBook같은 static method로 하는게 더 낫다.
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());

        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";

    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);

        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book book = (Book) itemService.findOne(itemId);

        BookForm bookForm = new BookForm();
        bookForm.setId(book.getId());
        bookForm.setName(book.getName());
        bookForm.setPrice(book.getPrice());
        bookForm.setStockQuantity(book.getStockQuantity());
        bookForm.setAuthor(book.getAuthor());
        bookForm.setIsbn(book.getIsbn());

        model.addAttribute("form", bookForm);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form, @PathVariable Long itemId) {
        // 또는 updateItemDto를 만들어서 하면 될듯.
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";
    }

/*
    @PostMapping("items/{itemId}/edit")
    public String updateItem_v1(@ModelAttribute("form") BookForm form, @PathVariable Long itemId) {
        // 실무 에선 itemId를 조심해야함. 뭔가 서비스 계층이던 뒷단이던 앞단이던 이 User가 이 Item에 대해 권한이 있는지에 대한 check가 필요.
        // 아니면 업데이트할 객체를 세션에 놓고하는 방법도 있긴하지만 요즘엔 세션을 안써서...

        Book book = new Book();
        book.setId(form.getId()); // 이런식으로 DB에 있던 애로 객체를 만든ㄷ거는 실제로 객체를
        // 실제로 만든 건 아니지만 jpa가 식별할 수 있는, 실제로 DB에 갔다온 객체라서 준영속 엔티티라고 하며
        // 영속성 컨텍스트가 더는 관리하지 않는 엔티티.
        // DB입장에선 이미 디비에 저장해서 관리안하는 Entity를 다시 getId로 식별자를 준거니까.
        // 이렇게 임의로 만들어낸 엔티티도 기존의 식별자를 가지면 준영속 엔티티로 볼 수 있다.
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        // 준영속 엔티티는 뭐가 문제냐면, 영속 엔티티는 JPA가 관리하기 때무넹 변경점을 딱 잡아서 DB 업데이트를 하지만
        // 준영속 엔티티는 JPA가 관리를 안하기 때문에 DB업데이트가 없다.
        // 이런 준영속 엔티티는 어떻게 데이털르 수정할 수 있을까?
        // 1. 변경감지기능 사용, 2. merge 사용.

        itemService.saveItem(book);

        return "redirect:/items";
    }*/
}