package JAVA00.week01;


import java.io.*;

public class MyClassLoader extends ClassLoader {

    public String classPath;

    public MyClassLoader(String xClassPath) {
        this.classPath = xClassPath;
    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classBates = getClassByte(classPath);
        if (classBates == null) {
            throw  new ClassNotFoundException();
        }
        for (int i = 0; i < classBates.length; i++) {
            classBates[i] = (byte)(255 - classBates[i]);
        }
        return super.defineClass(name,classBates,0 ,classBates.length);
    }

    private byte[] getClassByte (String path) {
        File file = new File(path);
        BufferedInputStream ins = null;
        ByteArrayOutputStream outs = null;
        try {
            if(!file.exists()) {
                return  null;
            }
            ins = new BufferedInputStream(new FileInputStream(file));
            outs = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = ins.read(buf)) != -1) {
                outs.write(buf,0,len);
            }
            return outs.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
