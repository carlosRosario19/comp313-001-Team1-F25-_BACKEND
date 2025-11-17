#!/bin/bash

echo "Waiting for DynamoDB to be ready..."
sleep 10

### -------------------------------------
###  REVIEWS TABLE
### -------------------------------------
echo "Checking if table exists..."
if aws dynamodb describe-table --table-name Reviews --endpoint-url http://dynamodb:8000 2>/dev/null; then
  echo "Table Reviews already exists. Skipping creation."
else
  echo "Creating DynamoDB table: Reviews"
  aws dynamodb create-table \
    --table-name Reviews \
    --attribute-definitions \
        AttributeName=reviewId,AttributeType=S \
        AttributeName=timeStamp,AttributeType=S \
        AttributeName=gameId,AttributeType=N \
    --key-schema \
        AttributeName=reviewId,KeyType=HASH \
        AttributeName=timeStamp,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST \
    --global-secondary-indexes '[
          {
            "IndexName": "gameId-timeStamp-index",
            "KeySchema": [
              {"AttributeName": "gameId", "KeyType": "HASH"},
              {"AttributeName": "timeStamp", "KeyType": "RANGE"}
            ],
            "Projection": {"ProjectionType": "ALL"}
          }
        ]' \
    --endpoint-url http://dynamodb:8000

  echo "Waiting for table to be created..."
  aws dynamodb wait table-exists \
    --table-name Reviews \
    --endpoint-url http://dynamodb:8000

  echo "Table created successfully!"
fi

### -------------------------------------
###  VOTES TABLE
### -------------------------------------
echo "Checking if 'Votes' table exists..."
if aws dynamodb describe-table --table-name Votes --endpoint-url http://dynamodb:8000 2>/dev/null; then
  echo "Table 'Votes' already exists. Skipping creation."
else
  echo "Creating DynamoDB table: Votes"
  aws dynamodb create-table \
    --table-name Votes \
    --attribute-definitions \
        AttributeName=reviewId,AttributeType=S \
        AttributeName=username,AttributeType=S \
    --key-schema \
        AttributeName=reviewId,KeyType=HASH \
        AttributeName=username,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url http://dynamodb:8000

  echo "Waiting for 'Votes' table to be created..."
  aws dynamodb wait table-exists \
    --table-name Votes \
    --endpoint-url http://dynamodb:8000

  echo "'Votes' table created successfully!"
fi


### -------------------------------------
###  LIST TABLES
### -------------------------------------
echo "Listing all tables:"
aws dynamodb list-tables --endpoint-url http://dynamodb:8000
echo "DynamoDB setup completed!"