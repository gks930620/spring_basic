package com.shop.basic.article;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//SpringbootTest가 아닌 단순테스트니까 @필요없음.
public class TestBasic {

    @BeforeAll
    public static void beforeAll(){  //static!
        System.out.println("테스트 시작전 준비!");
    }

    @BeforeEach
    public void beforeEach(){
        System.out.println("기본테스트 한개를 시작합니다. ");
    }
    @AfterEach
    public void afterEach(){
        System.out.println("기본테스트 한개가 끝났습니다.");
    }
    @AfterAll
    public static void afterAll(){
        System.out.println("마지막테스트까지 완료");
    }

    @Test
    public void junitQuiz1(){
        String name1="홍길동";
        String name2="홍길동";
        String name3="홍길은";

        assertThat(name1).isNotNull();
        assertThat(name2).isNotNull();
        assertThat(name3).isNotNull();

        assertThat(name1).isEqualTo(name2);
        assertThat(name1).isNotEqualTo(name3);
    }
    @Test
    public void junitQuiz2(){
        int number1=15;
        int number2=0;
        int number3=-5;

        assertThat(number1).isPositive();
        assertThat(number2).isZero();
        assertThat(number3).isNegative();

        assertThat(number1).isGreaterThan(number2);
        assertThat(number3).isLessThan(number1);
    }

}
