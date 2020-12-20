package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {
    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        // given
        Book book = em.find(Book.class, 1L);

        // when
        book.setName("새로운이름"); // 변경감지.
        /*트랜잭션이 커밋되면 ㅇ이 변경분에 대해서 자동으로 찾아서 업데이트
        * 그게 dirty checking, 변경 감지
        * 기본적으로 이 메커니즘으로 jpa 엔티티를 변경가능
        * */

        // then
    }
}
