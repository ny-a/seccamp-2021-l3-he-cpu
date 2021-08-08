#!/bin/bash

verilog_file_path=$1
verilog_file=`basename $verilog_file_path`
verilog_without_ext=${verilog_file%.*}
cat << EOF > tmp.ys
read_verilog ${verilog_file_path}
hierarchy -check -top ${verilog_without_ext}
proc; opt; fsm; opt; memory; opt
techmap; opt
flatten;
abc -g gates,MUX
clean -purge
write_json ${verilog_without_ext}.json
stat
EOF
yosys tmp.ys

dotnet run -p ../../Iyokan-L1/ -c Release ${verilog_without_ext}.json ${verilog_without_ext}_converted.json

echo "Build Done"
