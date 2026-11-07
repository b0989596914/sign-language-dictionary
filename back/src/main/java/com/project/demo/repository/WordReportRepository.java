package com.project.demo.repository;

import com.project.demo.model.WordReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordReportRepository extends JpaRepository<WordReport, Long> {
}