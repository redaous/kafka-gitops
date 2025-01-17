package com.devshawn.kafka.gitops.domain.state;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.inferred.freebuilder.FreeBuilder;

import java.util.List;

@FreeBuilder
@JsonDeserialize(builder = UserDetails.Builder.class)
public interface UserDetails {

    List<String> getRoles();

    class Builder extends UserDetails_Builder {
    }
}
