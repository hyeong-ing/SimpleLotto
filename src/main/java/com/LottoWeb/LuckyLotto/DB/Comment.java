package com.LottoWeb.LuckyLotto.DB;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@Table(name = "Comment_List")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 25)
    private String content;


    public Comment(String content) {
        this.content = content;
    }


}
