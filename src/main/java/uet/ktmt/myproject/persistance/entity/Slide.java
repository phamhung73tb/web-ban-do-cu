package uet.ktmt.myproject.persistance.entity;

import uet.ktmt.myproject.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Slide extends BaseEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "title_strong")
    private String titleStrong;

    @Column(name = "image")
    private String image;

    @Column(name = "link")
    private String link;
}
