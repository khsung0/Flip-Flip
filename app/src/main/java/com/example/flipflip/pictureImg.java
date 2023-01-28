package com.example.flipflip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class pictureImg {
    boolean isOpend = false;    //카드 open 됐는지 확인
    int imgIndex = 0;       //이미지 인덱스
}
