#!/bin/bash

IYOKAN="../../Iyokan/build/bin/iyokan"
IYOKAN_PACKET="../../Iyokan/build/bin/iyokan-packet"
REQ_FILE="_test_req_packet"
RES_FILE="_test_res_packet"
SKEY="_test_sk"
BKEY="_test_bk"

function gen_key() {
    echo "Preparing skey and bkey..."
    $IYOKAN_PACKET genkey --type tfhepp --out $SKEY 2> /dev/null
    if [ $? -ne 0 ]; then
        echo "failed genkey"
        exit 1
    fi

    $IYOKAN_PACKET genbkey --in $SKEY --out $BKEY 2> /dev/null
    if [ $? -ne 0 ]; then
        echo "failed genbkey"
        exit 1
    fi

    echo "Done."
}

# $1: Test name
# $2: Blueprint toml
# $3: Cycles
# $4: Input
# $5: Expected result
function test_run() {
    echo "Testing ${1}..."
    $IYOKAN_PACKET toml2packet --in $4 --out $REQ_FILE 2> /dev/null
    if [ $? -ne 0 ]; then
        echo "failed toml2packet"
        exit 1
    fi

    $IYOKAN_PACKET enc --key $SKEY --in $REQ_FILE --out $REQ_FILE 2> /dev/null
    if [ $? -ne 0 ]; then
        echo "failed enc"
        exit 1
    fi

    $IYOKAN tfhe --blueprint $2 --bkey $BKEY -i $REQ_FILE -o $RES_FILE -c $3 2> /dev/null
    if [ $? -ne 0 ]; then
        echo "failed iyokan"
        exit 1
    fi

    $IYOKAN_PACKET dec --key $SKEY --in $RES_FILE --out $RES_FILE 2> /dev/null
    if [ $? -ne 0 ]; then
        echo "failed dec"
        exit 1
    fi

    RESULT=`$IYOKAN_PACKET packet2toml --in $RES_FILE  2> /dev/null | grep "out" | sed -r 's/^.*\[(.+)\].*$/\1/' | awk -F, '{print $1 + ($2 * 256)}'`
    if [ $? -ne 0 ]; then
        echo "failed packet2toml"
        exit 1
    fi

    if [ $RESULT -ne $5 ]; then
        echo "failed. expected: $5 actual: $RESULT"
        exit 1
    fi
    echo "Done."
}


gen_key
test_run "ALU16bit-01" "test-alu-16bit.toml" "1" "test-alu-16bit-01.in" "22"
test_run "ALU16bit-01a" "test-alu-16bit.toml" "1" "test-alu-16bit-01a.in" "278"
test_run "ALU16bit-01b" "test-alu-16bit.toml" "1" "test-alu-16bit-01b.in" "768"
test_run "ALU16bit-02" "test-alu-16bit.toml" "1" "test-alu-16bit-02.in" "29"
test_run "ALU16bit-03" "test-alu-16bit.toml" "1" "test-alu-16bit-03.in" "36"
test_run "ALU16bit-04" "test-alu-16bit.toml" "1" "test-alu-16bit-04.in" "209"
test_run "ALU16bit-05" "test-alu-16bit.toml" "1" "test-alu-16bit-05.in" "245"

test_run "Sum16bit-01" "test-sum-16bit.toml" "15" "test-sum-16bit-01.in" "505"

rm _test_*
