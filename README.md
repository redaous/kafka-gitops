# kafka-dsf

Manage Apache Kafka topics and ACLs through a desired state file.

## Overview

This project allows you to manage Kafka topics and ACLs through use of a desired state file, much like Terraform and similar infrastructure-as-code projects. 

Topics and ACLs get defined in a YAML file. When run, `kafka-dsf` compares your desired state to the actual state of the cluster and generates a plan to execute against the cluster. This will make your topics and ACLs match your desired state.

## Usage

Run `kafka-dsf` to view the help output.

```bash
Usage: kafka-dsf [-hvV] [-f=<file>] [COMMAND]
Manage Kafka resources with a desired state file.
  -f, --file=<file>   Specify the desired state file.
  -h, --help          Display this help message.
  -v, --verbose       Show more detail during execution.
  -V, --version       Print the current version of this tool.
Commands:
  apply     Apply changes to Kafka resources.
  plan      Generate an execution plan of changes to Kafka resources.
  validate  Validates the desired state file.
```

## Configuration

Currently, configuring bootstrap servers and other properties is done via environment variables:

To configure properties, prefix them with `KAFKA_`. For example:

* `KAFKA_BOOTSTRAP_SERVERS`: Injects as `bootstrap.servers`
* `KAFKA_CLIENT_ID`: Injects as `client.id`

Additionally, we provide helpers for setting the `sasl.jaas.config` for clusters such as Confluent Cloud.

By setting:

* `KAFKA_SASL_JAAS_USERNAME`: Username to use
* `KAFKA_SASL_JAAS_PASSWORD`: Password to use

The following configuration is generated:

* `sasl.jaas.config`: `org.apache.kafka.common.security.plain.PlainLoginModule required username="USERNAME" password="PASSWORD";`

## State File

By default, `kafka-dsf` looks for `state.yaml` in the current directory. You can also use `kafka-dsf -f` to pass a file.

An example desired state file:

```yaml
topics:
  example-topic:
    partitions: 6
    replication: 3
    configs:
      cleanup.policy: compact

acls:
  example-topic-read-acl:
    name: example-topic
    type: TOPIC
    pattern: LITERAL
    principal: User:super.admin
    host: "*"
    operation: WRITE
    permission: ALLOW
```

## Contributing

Contributions are very welcome. See [CONTRIBUTING.md][contributing] for details.

## License

Copyright (c) 2020 Shawn Seymour.

Licensed under the [Apache 2.0 license][license].

[contributing]: ./CONTRIBUTING.md
[license]: ./LICENSE