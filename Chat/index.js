var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
app.get('/', function(req, res) {
	res.sendFile(__dirname + '/index.html');
});

io.on('connection', function(socket) {
	console.log('user connected');
	socket.on('chat message', function(name, msg) {
		name = name.toUpperCase();
		io.emit('chat message', name + ": " + msg);
	});
});

http.listen(3000, function() {
	console.log('listening on *:3000');
})