package org.springframework.batch.item.querydsl.integrationtest.reader.options;

import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.querydsl.integrationtest.TestBatchConfig;
import org.springframework.batch.item.querydsl.integrationtest.entity.Manufacture;
import org.springframework.batch.item.querydsl.integrationtest.entity.ManufactureRepository;
import org.springframework.batch.item.querydsl.integrationtest.job.QuerydslNoOffsetPagingItemReaderConfiguration;
import org.springframework.batch.item.querydsl.reader.expression.Expression;
import org.springframework.batch.item.querydsl.reader.options.QuerydslNoOffsetNumberOptions;
import org.springframework.batch.item.querydsl.reader.options.QuerydslNoOffsetStringOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.batch.item.querydsl.integrationtest.entity.QManufacture.manufacture;

/**
 * Created by jojoldu@gmail.com on 28/01/2020
 * Blog : http://jojoldu.tistory.com
 * Github : http://github.com/jojoldu
 */
@SpringBootTest(classes = {TestBatchConfig.class, QuerydslNoOffsetPagingItemReaderConfiguration.class})
class QuerydslNoOffsetOptionsTest {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private ManufactureRepository manufactureRepository;

    @AfterEach
    void teardown() {
        manufactureRepository.deleteAllInBatch();
    }

    @Test
    void path변수에서_필드명을_추출한다() {
        //given
        String expected = "id";
        NumberPath<Long> path = manufacture.id;

        //when
        QuerydslNoOffsetNumberOptions<Manufacture, Long> options = new QuerydslNoOffsetNumberOptions<>(path,  Expression.ASC);

        //then
        assertThat(options.getFieldName()).isEqualTo(expected);
    }

    @Test
    void Number_firstId_lastId_저장된다() {
        //given
        LocalDate txDate = LocalDate.of(2020,10,12);
        String name = "a";
        int categoryNo = 1;
        int expected1 = 1000;
        int expected2 = 2000;
        manufactureRepository.save(new Manufacture(name, expected1, categoryNo, txDate));
        manufactureRepository.save(new Manufacture(name, expected2, categoryNo, txDate));

        QuerydslNoOffsetNumberOptions<Manufacture, Long> options =
                new QuerydslNoOffsetNumberOptions<>(manufacture.id, Expression.ASC);

        Function<JPAQueryFactory, JPAQuery<Manufacture>> query = factory -> factory
                .selectFrom(manufacture)
                .where(manufacture.createDate.eq(txDate));
        JPAQuery<Manufacture> apply = query.apply(queryFactory);

        // when
        options.initKeys(apply, 0);

        // then
        assertThat(options.getCurrentId() <options.getLastId()).isTrue();
    }

    @Test
    void String_firstId_lastId_저장된다() {
        //given
        LocalDate txDate = LocalDate.of(2020,10,12);
        int categoryNo = 1;
        long price = 1000;
        String expected1 = "a";
        String expected2 = "b";
        manufactureRepository.save(new Manufacture(expected1, price, categoryNo, txDate));
        manufactureRepository.save(new Manufacture(expected2, price, categoryNo, txDate));

        QuerydslNoOffsetStringOptions<Manufacture> options =
                new QuerydslNoOffsetStringOptions<>(manufacture.name, Expression.DESC);

        Function<JPAQueryFactory, JPAQuery<Manufacture>> query = factory -> factory
                .selectFrom(manufacture)
                .where(manufacture.createDate.eq(txDate));
        JPAQuery<Manufacture> apply = query.apply(queryFactory);

        // when
        options.initKeys(apply, 0);

        // then
        assertThat(options.getCurrentId()).isEqualTo(expected2);
        assertThat(options.getLastId()).isEqualTo(expected1);
    }

    @Test
    void groupBy절인지_확인_할수_있다() throws Exception {
        //given
        LocalDate txDate = LocalDate.of(2020,10,12);
        QuerydslNoOffsetStringOptions<Manufacture> options =
                new QuerydslNoOffsetStringOptions<>(manufacture.name, Expression.DESC);

        Function<JPAQueryFactory, JPAQuery<Manufacture>> query = factory -> factory
                .selectFrom(manufacture)
                .where(manufacture.createDate.eq(txDate))
                .groupBy(manufacture.name);

        JPAQuery<Manufacture> apply = query.apply(queryFactory);

        //when
        boolean isGroupBy = options.isGroupByQuery(apply);

        //then
        assertThat(isGroupBy).isTrue();
    }

    @Test
    void group만_있으면_false() throws Exception {
        //given
        QuerydslNoOffsetStringOptions<Manufacture> options =
                new QuerydslNoOffsetStringOptions<>(manufacture.name, Expression.DESC);

        //when
        boolean isGroupBy = options.isGroupByQuery("select group from class");

        //then
        assertThat(isGroupBy).isFalse();
    }
}
