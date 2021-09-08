import sys
import argparse

B0_INSTRUCTION = 0xA000
EOL = ';\n'


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('filename', nargs='?', default='output.mif')
    parser.add_argument(
        '-t', '--toml', help='Output as toml', action='store_true')
    args = parser.parse_args()

    values = []
    valid_length = 0
    width = 0
    depth = 0

    with open(args.filename) as file:
        current_depth = 0
        current_length = 0
        address_radix = ''
        data_radix = ''
        is_content = False
        for line in file.readlines():
            if is_content:
                if depth <= current_depth:
                    break
                value = int(line.strip(EOL).split(':')[1])
                if value < 0:
                    value += 1 << width
                if args.toml:
                    bytes = value
                    while 0 < bytes:
                        values.append(bytes & 0xFF)
                        bytes >>= 8
                        current_length += 1
                else:
                    values.append(value)
                    current_length += 1
                if value != B0_INSTRUCTION:
                    valid_length = current_length
                current_depth += 1
            else:
                if 'WIDTH' in line:
                    width = int(line.strip(EOL).split('=')[1])
                if 'DEPTH' in line:
                    depth = int(line.strip(EOL).split('=')[1])
                if 'ADDRESS_RADIX' in line:
                    address_radix = line.strip(EOL).split('=')[1].strip(' ')
                if 'DATA_RADIX' in line:
                    data_radix = line.strip(EOL).split('=')[1].strip(' ')
                if 'CONTENT BEGIN' in line:
                    is_content = True

    if args.toml:
        print('[[rom]]')
        print('name = "rom"')
        print(f'size = {width * depth}')
        print('bytes = [')
        for value in values[:valid_length]:
            print(f'  {value},')
        print(']')
    else:
        print('Seq(', end='')
        print(', '.join(map(lambda x: hex(x), values[:valid_length])), end='')
        print(')')
