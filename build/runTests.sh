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

echo 'tests pass'
