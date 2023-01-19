#include "server.hpp"
#include <zmq.hpp>

server::server(
    std::string server_binded_address,
    std::function<std::string(std::string input_str)> on_input_received

) {

  this->server_binded_address = server_binded_address;
  this->on_input_received = on_input_received;
}

void server::start() {

  printf("starting server at address %s\n",
         this->server_binded_address.c_str());

  zmq::context_t context(2);
  zmq::socket_t socket(context, zmq::socket_type::rep);
  socket.bind(this->server_binded_address);

  while (true) {
    zmq::message_t request;

    //  Wait for next request from client
    socket.recv(request, zmq::recv_flags::none);

    //  Do some 'work' and get an output string
    std::string request_str = request.to_string();
    std::string out_str = this->on_input_received(request_str);

    //  Send reply back to client
    zmq::message_t reply(out_str.size());
    memcpy(reply.data(), out_str.c_str(), out_str.size());
    socket.send(reply, zmq::send_flags::none);
  }
}
