package com.devJeans.rabbit.domain;

import org.checkerframework.checker.units.qual.A;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Photo photo;

    @Column(nullable = false)
    private ReportType reportType;

    public Report(Account user, Photo photo, ReportType reportType) {
        this.user = user;
        this.photo = photo;
        this.reportType = reportType;
    }

    public Report() {

    }

    public Account getUser() {
        return user;
    }

    public Photo getPhoto() {
        return photo;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public enum ReportType {
        OBSCENITY, VIOLENCE, INAPPROPRIATE
    }
}
