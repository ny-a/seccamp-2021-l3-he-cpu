import sys

if __name__ == '__main__':
    with open('_test_capitalize-string.out') as input:
        values = ''.join(input.readlines()).replace('\n', '')
        values = values.split('bytes = [')[1]
        values = values.split(']')[0]
        values = values.split(',')
        for char in values:
            if char != '':
                print(chr(int(char)), end='')
        print()


