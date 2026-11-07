package com.project.demo.repository;

import com.project.demo.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {

    /**
     * 自定義查詢：
     * 1. 先按 isPinned 降冪排序 (true 會在 false 前面)
     * 2. 再按 publishDate 降冪排序 (最新的在最上面)
     */
    List<News> findAllByOrderByIsPinnedDescPublishDateDesc();
}