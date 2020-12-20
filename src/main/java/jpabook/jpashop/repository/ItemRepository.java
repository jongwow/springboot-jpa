package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        // item은 jpa에 저장하기 전까지 id값이 없음(새로 생성한 경우)
        // 없으면 신규로 등록(persist) 있으면 merge(update). 완벽하게 이 설명은 아니지만 일단 이렇게 직관적 이해
        if(item.getId() == null){
            em.persist(item);
        }else {
            em.merge(item); // 일종의 update같은 것.
            // 이걸 잘 설명은 안하는데 실무에서 쓸일이 많진 않음. 화면에서 entity가 넘어오고 해야 merge를 쓰는 방식을 앎.
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i ", Item.class).getResultList();
    }
}
