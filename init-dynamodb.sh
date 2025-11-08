#!/bin/bash

echo "Waiting for DynamoDB to be ready..."
sleep 10

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
    --key-schema \
        AttributeName=reviewId,KeyType=HASH \
        AttributeName=timeStamp,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url http://dynamodb:8000

  echo "Waiting for table to be created..."
  aws dynamodb wait table-exists \
    --table-name Reviews \
    --endpoint-url http://dynamodb:8000

  echo "Table created successfully!"
fi

echo "Listing all tables:"
aws dynamodb list-tables --endpoint-url http://dynamodb:8000
echo "DynamoDB setup completed!"