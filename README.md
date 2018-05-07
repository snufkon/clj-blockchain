# clj-blockchain

Implementation of [this post](https://hackernoon.com/learn-blockchains-by-building-one-117428612f46) written by Clojure.

- code of original post: https://github.com/dvf/blockchain


## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen


## Demo

**start up two servers**

```
$ lein ring server-headless 3000
2018-05-07 21:04:58.064:INFO:oejs.Server:jetty-7.6.13.v20130916
2018-05-07 21:04:58.143:INFO:oejs.AbstractConnector:Started SelectChannelConnector@0.0.0.0:3000
Started server on port 3000
```

```
$ lein ring server-headless 3001
2018-05-07 21:05:08.553:INFO:oejs.Server:jetty-7.6.13.v20130916
2018-05-07 21:05:08.629:INFO:oejs.AbstractConnector:Started SelectChannelConnector@0.0.0.0:3001
Started server on port 3001
```

**register node (request to port 3000)**

```
curl -X POST -H "Content-Type: application/json" -d '{
    "nodes": ["http://localhost:3001"]
}' "http://localhost:3000/nodes/register" | jq .
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   114  100    72  100    42    306    178 --:--:-- --:--:-- --:--:--   309
{
  "message": "New nodes have been added",
  "total-nodes": [
    "localhost:3001"
  ]
}
```

**mine (request to port 3001)**

```
curl http://localhost:3001/mine | jq .
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   227  100   227    0     0    248      0 --:--:-- --:--:-- --:--:--   248
{
  "message": "New Block Forged",
  "index": 2,
  "transactions": [
    {
      "sender": "0",
      "recipient": "5e6e7f01b8f34eb3bfc1e6668ccc2cfb",
      "amount": 1
    }
  ],
  "proof": 35293,
  "previous-hash": "90f1eb8b6d6fff81ac2e1a3b84d82c650bae960f435929b1befdca236ce5f9da"
}
```

**chain (request to port 3001)**

```
curl http://localhost:3001/chain | jq .
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   335  100   335    0     0  21951      0 --:--:-- --:--:-- --:--:-- 22333
{
  "chain": [
    {
      "index": 1,
      "timestamp": 1525694708488,
      "transactions": [],
      "proof": 100,
      "previous-hash": "1"
    },
    {
      "index": 2,
      "timestamp": 1525694848842,
      "transactions": [
        {
          "sender": "0",
          "recipient": "5e6e7f01b8f34eb3bfc1e6668ccc2cfb",
          "amount": 1
        }
      ],
      "proof": 35293,
      "previous-hash": "90f1eb8b6d6fff81ac2e1a3b84d82c650bae960f435929b1befdca236ce5f9da"
    }
  ],
  "length": 2
}
```

**chain (request to port 3000)**


```
url http://localhost:3000/chain | jq .
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   110  100   110    0     0   3275      0 --:--:-- --:--:-- --:--:--  3333
{
  "chain": [
    {
      "index": 1,
      "timestamp": 1525694698003,
      "transactions": [],
      "proof": 100,
      "previous-hash": "1"
    }
  ],
  "length": 1
}
```

**resolve (request to port 3000)**

```
curl http://localhost:3000/nodes/resolve | jq .

  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   363  100   363    0     0   1598      0 --:--:-- --:--:-- --:--:--  1592
{
  "message": "Our chain was replaced",
  "new-chain": [
    {
      "index": 1,
      "timestamp": 1525694708488,
      "transactions": [],
      "proof": 100,
      "previous-hash": "1"
    },
    {
      "index": 2,
      "timestamp": 1525694848842,
      "transactions": [
        {
          "sender": "0",
          "recipient": "5e6e7f01b8f34eb3bfc1e6668ccc2cfb",
          "amount": 1
        }
      ],
      "proof": 35293,
      "previous-hash": "90f1eb8b6d6fff81ac2e1a3b84d82c650bae960f435929b1befdca236ce5f9da"
    }
  ]
}
```

**chain (request to port 3000)**

```
curl http://localhost:3000/chain | jq .
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   335  100   335    0     0  21074      0 --:--:-- --:--:-- --:--:-- 22333
{
  "chain": [
    {
      "index": 1,
      "timestamp": 1525694708488,
      "transactions": [],
      "proof": 100,
      "previous-hash": "1"
    },
    {
      "index": 2,
      "timestamp": 1525694848842,
      "transactions": [
        {
          "sender": "0",
          "recipient": "5e6e7f01b8f34eb3bfc1e6668ccc2cfb",
          "amount": 1
        }
      ],
      "proof": 35293,
      "previous-hash": "90f1eb8b6d6fff81ac2e1a3b84d82c650bae960f435929b1befdca236ce5f9da"
    }
  ],
  "length": 2
}
```

## Test

You can run test by following

    lein test

## License

Copyright © 2018 FIXME
