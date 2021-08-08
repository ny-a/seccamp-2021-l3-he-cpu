#!/bin/bash

# thanks to https://www.marketechlabo.com/bash-batch-best-practice/
set -e -o pipefail

function raise() {
  echo $1 1>&2
  return 1
}

err_buf=""
function err() {
  # Usage: trap 'err ${LINENO[0]} ${FUNCNAME[1]}' ERR
  status=$?
  lineno=$1
  func_name=${2:-main}
  err_str="ERROR: [`date +'%Y-%m-%d %H:%M:%S'`] ${SCRIPT}:${func_name}() returned non-zero exit status ${status} at line ${lineno}"
  echo ${err_str}
  err_buf+=${err_str}
}

function finally() {
  echo "end"
}


curl -sL https://packages.microsoft.com/config/ubuntu/20.04/packages-microsoft-prod.deb -o packages-microsoft-prod.deb
sudo dpkg -i packages-microsoft-prod.deb
rm packages-microsoft-prod.deb
sudo apt-get update && sudo apt-get install -y \
    build-essential git curl software-properties-common openjdk-11-jre \
    cmake clang clang-9 bison flex libreadline-dev dotnet-sdk-3.1 \
    gawk tcl-dev libffi-dev graphviz xdot pkg-config python3 libboost-system-dev \
    libboost-python-dev libboost-filesystem-dev zlib1g-dev yosys
git clone --recursive https://github.com/virtualsecureplatform/Iyokan
git clone https://github.com/virtualsecureplatform/Iyokan-L1

cd Iyokan-L1
dotnet build

cd ../Iyokan
mkdir build
cd build
cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_C_COMPILER=clang -DCMAKE_CXX_COMPILER=clang++ ..
make -j$(nproc)

