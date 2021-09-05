import sys

if __name__ == '__main__':
    with open('_test_capitalize-string.in', 'w') as output:
        output.writelines([
            '[[bits]]\n',
            'size = 256\n',
            'name = "in"\n',
        ])
        values = []
        for char in ' '.join(sys.argv[1:]):
            values.append(str(ord(char)))
        array = ', '.join(values)
        output.write(f'bytes = [ {array} ]\n')


