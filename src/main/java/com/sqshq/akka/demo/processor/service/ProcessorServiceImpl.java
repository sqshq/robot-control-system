package com.sqshq.akka.demo.processor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessorServiceImpl implements ProcessorService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void compute() {
        log.info("ProcessorService computed");
    }
}
