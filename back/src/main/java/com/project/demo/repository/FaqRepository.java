package com.project.demo.repository;

import com.project.demo.model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Integer> {

    /**
     * 自定義查詢：
     * 按 sortOrder 升冪排序 (0, 1, 2...)
     */
    List<Faq> findAllByOrderBySortOrderAsc();

    /**
     * 額外功能：按分類篩選問題
     */
    List<Faq> findByCategoryOrderBySortOrderAsc(String category);
}