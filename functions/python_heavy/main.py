from flask import Response

def heavy(request):
    message = request.data.decode("utf-8")
    sortedMessage = sorted(message)
    result = ''.join(sortedMessage)
    return Response(result, mimetype='text/plain')
