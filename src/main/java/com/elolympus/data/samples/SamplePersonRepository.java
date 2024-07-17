package com.elolympus.data.samples;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

    public interface SamplePersonRepository
            extends
                JpaRepository<SamplePerson, Long>,
                JpaSpecificationExecutor<SamplePerson> {



    }
