package com.flightops.platform.repository;

import com.flightops.platform.domain.CrewMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
}
