"""
 This file is executed when the Python interpreter is started.
 Use this file to configure all your necessary python code.

"""

import json
import sys


print("Starting 'bootstrap.py'")
print(sys.path)


def router(args):
    """
    Defines the router function that routes by function name.

    :param args: JSON arguments
    :return: JSON response
    """
    values = json.loads(args)

    try:
        function = routes[values.get('function')]

        status = 'ok'
        res = function(values)
    except KeyError:
        status = 'fail'
        res = None

    return json.dumps({
        'status': status,
        'result': res,
    })


def greet(args):
    """Simple function that greets someone."""
    return 'Hello %s' % args['name']


def add(args):
    """Simple function to add two numbers."""
    return args['a'] + args['b']


def mul(args):
    """Simple function to multiply two numbers."""
    return args['a'] * args['b']


routes = {
    'greet': greet,
    'add': add,
    'mul': mul,
}
