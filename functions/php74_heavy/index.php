<?php

use Psr\Http\Message\ServerRequestInterface;

function php74_heavy(ServerRequestInterface $request)
{
    $string = $request->getBody()->getContents();
    $stringParts = str_split($string);
    sort($stringParts);
    return implode($stringParts);
}
