#!/bin/bash
# Runs tests

# Check return code and exit on failure 
function check_return_code_ok {

    if [ $1 -ne 0 ]; then
        echo ':-{'
        exit $1
    fi
}

# Check return code and exit if no failure 
function check_return_code_not_ok {

    if [ $1 -eq 0 ]; then
        echo ':-{'
        exit $1
    fi
}

echo 'file_scan - fall back to EICAR ok'
./scripts/file_scan.sh | grep malware
check_return_code_ok $?

echo 'file_scan - scan file ok'
./scripts/file_scan.sh  ./scripts/file_scan.sh | grep -v malware
check_return_code_ok $?

echo 'file_scan - scan directory ok'
./scripts/file_scan.sh ./scripts
check_return_code_ok $?

echo 'file_scan - scan badpath not ok'
./scripts/file_scan.sh badpath
check_return_code_not_ok $?


echo 'file_scan - tests pass'
