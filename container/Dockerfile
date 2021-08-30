FROM ubuntu:20.04

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

RUN apt-get update -y && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y sudo openjdk-11-jdk gnupg2 curl apt-transport-https gcc libtinfo5 python python3 python3-pip && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    echo "deb [arch=amd64] https://storage.googleapis.com/bazel-apt stable jdk1.8" | tee /etc/apt/sources.list.d/bazel.list && \
    curl -fsSL https://bazel.build/bazel-release.pub.gpg | gpg --dearmor > /etc/apt/trusted.gpg.d/bazel.gpg && \
    apt-get update -y && \
    apt-get install -y sbt bazel-4.0.0

RUN git clone https://github.com/naoki9911/seccamp2021-l3 /root/seccamp2021-l3 && \
    cd /root/seccamp2021-l3 && \
    ./install_toolchain.sh && \
    cd ex1 && \
    sbt run

RUN git clone --recursive https://github.com/google/xls /root/xls && \
    cd /root/xls && \
    bazel-4.0.0 build -c opt //xls/contrib/xlscc:xlscc //xls/tools:opt_main //xls/tools:codegen_main
