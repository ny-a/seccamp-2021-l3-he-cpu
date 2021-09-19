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

    $IYOKAN tfhe $IYOKAN_OPTION --blueprint $blueprint_toml --bkey $BKEY -i $REQ_FILE -o $RES_FILE -c $cycles 2> /dev/null
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
test_run "fib-0" "test-simple-cpu.toml" "32" "0" "0"
if [[ "$TEST_UNTIL" = "0" ]]; then exit 0; fi
test_run "fib-1" "test-simple-cpu.toml" "44" "1" "1"
if [[ "$TEST_UNTIL" = "1" ]]; then exit 0; fi
test_run "fib-2" "test-simple-cpu.toml" "56" "2" "1"
if [[ "$TEST_UNTIL" = "2" ]]; then exit 0; fi
test_run "fib-3" "test-simple-cpu.toml" "68" "3" "2"
if [[ "$TEST_UNTIL" = "3" ]]; then exit 0; fi
test_run "fib-4" "test-simple-cpu.toml" "80" "4" "3"
if [[ "$TEST_UNTIL" = "4" ]]; then exit 0; fi
test_run "fib-5" "test-simple-cpu.toml" "92" "5" "5"
if [[ "$TEST_UNTIL" = "5" ]]; then exit 0; fi
test_run "fib-6" "test-simple-cpu.toml" "104" "6" "8"
if [[ "$TEST_UNTIL" = "6" ]]; then exit 0; fi
test_run "fib-7" "test-simple-cpu.toml" "116" "7" "13"
if [[ "$TEST_UNTIL" = "7" ]]; then exit 0; fi
test_run "fib-8" "test-simple-cpu.toml" "128" "8" "21"
if [[ "$TEST_UNTIL" = "8" ]]; then exit 0; fi
test_run "fib-9" "test-simple-cpu.toml" "140" "9" "34"
if [[ "$TEST_UNTIL" = "9" ]]; then exit 0; fi
test_run "fib-10" "test-simple-cpu.toml" "152" "10" "55"
if [[ "$TEST_UNTIL" = "10" ]]; then exit 0; fi
test_run "fib-11" "test-simple-cpu.toml" "164" "11" "89"
if [[ "$TEST_UNTIL" = "11" ]]; then exit 0; fi
test_run "fib-12" "test-simple-cpu.toml" "176" "12" "144"
if [[ "$TEST_UNTIL" = "12" ]]; then exit 0; fi
test_run "fib-13" "test-simple-cpu.toml" "188" "13" "233"
if [[ "$TEST_UNTIL" = "13" ]]; then exit 0; fi
test_run "fib-14" "test-simple-cpu.toml" "200" "14" "377"
if [[ "$TEST_UNTIL" = "14" ]]; then exit 0; fi
test_run "fib-15" "test-simple-cpu.toml" "212" "15" "610"
if [[ "$TEST_UNTIL" = "15" ]]; then exit 0; fi
test_run "fib-16" "test-simple-cpu.toml" "224" "16" "987"
if [[ "$TEST_UNTIL" = "16" ]]; then exit 0; fi
test_run "fib-17" "test-simple-cpu.toml" "236" "17" "1597"
if [[ "$TEST_UNTIL" = "17" ]]; then exit 0; fi
test_run "fib-18" "test-simple-cpu.toml" "248" "18" "2584"
if [[ "$TEST_UNTIL" = "18" ]]; then exit 0; fi
test_run "fib-19" "test-simple-cpu.toml" "260" "19" "4181"
if [[ "$TEST_UNTIL" = "19" ]]; then exit 0; fi
test_run "fib-20" "test-simple-cpu.toml" "272" "20" "6765"
if [[ "$TEST_UNTIL" = "20" ]]; then exit 0; fi
test_run "fib-21" "test-simple-cpu.toml" "284" "21" "10946"
if [[ "$TEST_UNTIL" = "21" ]]; then exit 0; fi
test_run "fib-22" "test-simple-cpu.toml" "296" "22" "17711"
if [[ "$TEST_UNTIL" = "22" ]]; then exit 0; fi
test_run "fib-23" "test-simple-cpu.toml" "308" "23" "28657"

rm _test_*
