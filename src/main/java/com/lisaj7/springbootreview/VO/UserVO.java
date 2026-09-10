/*
* VO(View Object) → 专门给前端展示
*
* UserVO = 展示给前端的数据
* */

package com.lisaj7.springbootreview.VO;

import lombok.Data;

@Data
public class UserVO {

    private Long id;

    private String name;

    private String ageText;
}
