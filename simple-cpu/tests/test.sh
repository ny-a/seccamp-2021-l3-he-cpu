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
    test_name="$1"
    echo "Testing ${test_name}..."
    blueprint_toml="$2"
    cycles="$3"
    input="$4"
    expected="$5"

    input_file="./_test_simple-cpu-fib-${input}.in"

    cat ./test-simple-cpu-fib-template.in | sed "s/INPUT/$input/" > $input_file

    $IYOKAN_PACKET toml2packet --in $input_file --out $REQ_FILE 2> /dev/null
    if [ $? -ne 0 ]; then
        echo "failed toml2packet"
        exit 1
    fi

    $IYOKAN_PACKET enc --key $SKEY --in $REQ_FILE --out $REQ_FILE 2> /dev/null
    if [ $? -ne 0 ]; then
        echo "failed enc"
        exit 1
    fi

    $IYOKAN tfhe --blueprint $blueprint_toml --bkey $BKEY -i $REQ_FILE -o $RES_FILE -c $cycles 2> /dev/null
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

    if [ $RESULT -ne "$expected" ]; then
        echo "failed. expected: ${expected} actual: $RESULT"
        exit 1
    fi
    echo "Done."
}


gen_key
test_run "fib-0" "test-simple-cpu.toml" "100" "0" "0"
test_run "fib-1" "test-simple-cpu.toml" "120" "1" "1"
test_run "fib-2" "test-simple-cpu.toml" "140" "2" "1"
test_run "fib-3" "test-simple-cpu.toml" "160" "3" "2"
test_run "fib-4" "test-simple-cpu.toml" "180" "4" "3"
test_run "fib-5" "test-simple-cpu.toml" "200" "5" "5"
test_run "fib-6" "test-simple-cpu.toml" "220" "6" "8"
test_run "fib-7" "test-simple-cpu.toml" "240" "7" "13"
test_run "fib-8" "test-simple-cpu.toml" "260" "8" "21"
test_run "fib-9" "test-simple-cpu.toml" "280" "9" "34"
test_run "fib-10" "test-simple-cpu.toml" "300" "10" "55"
test_run "fib-11" "test-simple-cpu.toml" "320" "11" "89"
test_run "fib-12" "test-simple-cpu.toml" "340" "12" "144"
test_run "fib-13" "test-simple-cpu.toml" "360" "13" "233"
test_run "fib-14" "test-simple-cpu.toml" "380" "14" "377"
test_run "fib-15" "test-simple-cpu.toml" "400" "15" "610"
test_run "fib-16" "test-simple-cpu.toml" "420" "16" "987"
test_run "fib-17" "test-simple-cpu.toml" "440" "17" "1597"
test_run "fib-18" "test-simple-cpu.toml" "460" "18" "2584"
test_run "fib-19" "test-simple-cpu.toml" "480" "19" "4181"
test_run "fib-20" "test-simple-cpu.toml" "500" "20" "6765"

rm _test_*
