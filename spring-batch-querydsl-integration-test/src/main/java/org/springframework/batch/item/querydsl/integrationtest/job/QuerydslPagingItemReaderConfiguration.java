package org.springframework.batch.item.querydsl.integrationtest.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.querydsl.integrationtest.entity.Manufacture;
import org.springframework.batch.item.querydsl.integrationtest.entity.ManufactureBackup;
import org.springframework.batch.item.querydsl.reader.QuerydslPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.transaction.PlatformTransactionManager;

import static org.springframework.batch.item.querydsl.integrationtest.entity.QManufacture.manufacture;


/**
 * Created by jojoldu@gmail.com on 06/10/2019
 * Blog : http://jojoldu.tistory.com
 * Github : http://github.com/jojoldu
 */

@Slf4j
@RequiredArgsConstructor
@Configuration
public class QuerydslPagingItemReaderConfiguration {
    public static final String JOB_NAME = "querydslPagingReaderJob";

    private final EntityManagerFactory emf;
    private final QuerydslPagingItemReaderJobParameter jobParameter;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private int chunkSize;

    @Value("${chunkSize:1000}")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Bean
    @JobScope
    public QuerydslPagingItemReaderJobParameter jobParameter() {
        return new QuerydslPagingItemReaderJobParameter();
    }

    @Bean
    public Job job() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(step())
                .build();
    }

    @Bean
    @JobScope
    public Step step() {
        return new StepBuilder("querydslPagingReaderStep", jobRepository)
                .<Manufacture, ManufactureBackup>chunk(chunkSize, platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    @JobScope
    public QuerydslPagingItemReader<Manufacture> reader() {
        return new QuerydslPagingItemReader<>(emf, chunkSize, queryFactory -> queryFactory
                .selectFrom(manufacture)
                .where(manufacture.createDate.eq(jobParameter.getTxDate())));
    }

    private ItemProcessor<Manufacture, ManufactureBackup> processor() {
        return ManufactureBackup::new;
    }

    @Bean
    @JobScope
    public JpaItemWriter<ManufactureBackup> writer() {
        return new JpaItemWriterBuilder<ManufactureBackup>()
                .entityManagerFactory(emf)
                .build();
    }
}
