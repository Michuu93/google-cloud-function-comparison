<?php

use Psr\Http\Message\ServerRequestInterface;

function hello_world(ServerRequestInterface $request)
{
    return 'Hello World!';
}
