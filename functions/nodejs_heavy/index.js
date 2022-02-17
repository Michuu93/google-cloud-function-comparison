exports.heavy = (req, res) => {
    let sortedMessage = [];
    if (typeof req.body === 'string' || req.body instanceof String) {
        let message = req.body.split('');
        sortedMessage = message.sort();
    }
    res.status(200).send(sortedMessage.join(''));
};
