#!/bin/bash

IYOKAN="../Iyokan/build/bin/iyokan"
IYOKAN_PACKET="../Iyokan/build/bin/iyokan-packet"
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
# $@: Input
function run() {
    test_name="$1"
    echo "Testing ${test_name}..."
    blueprint_toml="$2"
    cycles="$3"
    shift 3

    python3 ./gen_input.py "$@"
    $IYOKAN_PACKET toml2packet --in "_test_capitalize-string.in" --out $REQ_FILE 2> /dev/null
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

    RESULT=`$IYOKAN_PACKET packet2toml --in $RES_FILE  2> /dev/null > "_test_capitalize-string.out"`
    if [ $? -ne 0 ]; then
        echo "failed packet2toml"
        exit 1
    fi

    python3 ./get_output.py

    echo "Done."
}

gen_key
run "CapitalizeString" "capitalize-string.toml" "1" "$@"

rm _test_*
