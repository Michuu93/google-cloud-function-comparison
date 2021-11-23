exports.heavy = (req, res) => {
    let message = req.body.split('');
    let sortedMessage = message.sort();
    res.status(200).send(sortedMessage.join(''));
};
