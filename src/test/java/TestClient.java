import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {

    private Socket client;	//����ͻ����׽���

    //�����ͻ��˺���
    void getClient()
    {
        try {
            client = new Socket("127.0.0.1", 8080);	//�����ͻ��ˣ�ʹ�õ�IPΪ127.0.0.1���˿ںͷ�����һ��Ϊ8080
            System.out.println("�ͻ��˽����ɹ���");

            setClientMessage();		//���ÿͻ�����Ϣд�뺯��
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //����ͻ�����Ϣд�뺯��
    void setClientMessage()
    {
        try {
            OutputStream pt = client.getOutputStream();		//�����ͻ�����Ϣ�����
            String printText = "��������ã����ǿͻ��ˣ�";
            pt.write(printText.getBytes());		//�Զ����Ƶ���ʽ����Ϣ�������

            InputStream input = client.getInputStream();	//�����ͻ�����Ϣ������
            byte [] b = new byte[1024];		//�����ֽ�����
            int len = input.read(b);	//��ȡ���յĶ�������Ϣ��
            String data = new String(b, 0,len);
            System.out.println("�յ���������Ϣ��" + data);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            //����ͻ�����Ϣ����Ϊ�գ���˵���ͻ����Ѿ��������ӣ��رտͻ���
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void main(String[] args) {
        //���ɿͻ��������
        TestClient myClient  = new TestClient();
        myClient.getClient();
    }

}
