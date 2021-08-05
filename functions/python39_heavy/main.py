def heavy(request):
    message = request.data.decode("utf-8")
    sortedMessage = sorted(message)
    result = ''.join(sortedMessage)
    return result
