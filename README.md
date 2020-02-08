# grpc_api_examples

Java application with gRPC API examples

# start
- run `generateProto` gradle task to generate gRPC files based on API definition in `.proto` files. 
Proto files are under `src/main/proto/` directory

- start specific server class and then run one of the corresponding clients to send gRPC request.

# SSL
Under `ssl` directory are neccessery files for secure server and client examples.
Some server and clients have specific ssl configuration.

For `movie` example you can play with fully compatible gRPC Python server and client from: 
https://github.com/WojciechGalecki/python_grpc_api

# gRPC documentation: 
https://grpc.io/
