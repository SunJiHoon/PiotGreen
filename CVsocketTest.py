import socket

def send_command(command):
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server_address = ('127.0.0.1', 9999)  # 수신 측 IP와 포트 설정
    try:
        # 메시지 전송
        sock.sendto(command.encode('utf-8'), server_address)
        print(f"Command '{command}' sent to {server_address}")
    finally:
        sock.close()

if __name__ == "__main__":
    while True:
        command = input("Enter command (intrusion_detection:danger:on/off or 'q' to quit): ")
        if command == 'q':
            break
        elif command in ["intrusion_detection:danger:on", "intrusion_detection:danger:off"]:
            send_command(command)
        else:
            print("Invalid command. Please enter 'intrusion_detection:danger:on' or 'intrusion_detection:danger:off'.")
