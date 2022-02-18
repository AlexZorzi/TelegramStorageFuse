package com.alles.telegramstoragefuse;


import com.alles.telegramstoragelib.TelegramStorage;
import jnr.ffi.Platform;
import jnr.ffi.Pointer;
import jnr.ffi.types.off_t;
import jnr.ffi.types.size_t;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.FuseStubFS;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class Main extends FuseStubFS {
    public TelegramStorage storage;
    public ConcurrentHashMap<String, byte[]> file_cache = new ConcurrentHashMap<>();

    @Override
    public int getattr(String pathFull, FileStat stat) {
        int res = 0;

        if (Objects.equals(pathFull, "/")) {
            stat.st_mode.set(FileStat.S_IFDIR | 0755);
            stat.st_nlink.set(2);
        }else if (pathFull.charAt(1) != '.' || !"/autorun.inf".equals(pathFull)) {
            stat.st_mode.set(FileStat.S_IFREG | 0444);
            stat.st_nlink.set(1);
            DbEntry dbentry = H2Select.Select(pathFull);
            stat.st_size.set(Integer.parseInt(dbentry.ByteSize));

            //res = -ErrorCodes.ENOENT();
        }
        return res;
    }

    @Override
    public int readdir(String path, Pointer buf, FuseFillDir filter, @off_t long offset, FuseFileInfo fi) {
        if (!"/".equals(path)) {
            return -ErrorCodes.ENOENT();
        }

        filter.apply(buf, ".", null, 0);
        filter.apply(buf, "..", null, 0);
        ArrayList<DbEntry> dbentry = H2Select.SelectAll();

        for (DbEntry entry: dbentry){
            filter.apply(buf, entry.Filename, null, 0);
        }
        return 0;
    }

    @Override
    public int open(String pathFull, FuseFileInfo fi) {
        if (pathFull.charAt(1) == '.' | "/autorun.inf".equals(pathFull)){
            return -ErrorCodes.ENOENT();
        }

        return 0;
    }

    @Override
    public int read(String pathFull, Pointer buf, @size_t long size, @off_t long offset, FuseFileInfo fi) {
        if (pathFull.charAt(1) == '.' | "/autorun.inf".equals(pathFull)){
            return -ErrorCodes.ENOENT();
        }
        DbEntry file = H2Select.Select(pathFull);
        try {
            byte[] bytes;
            if(!this.file_cache.containsKey(pathFull)){
                System.out.println(" Downloading "+pathFull);
                InputStream indata =  storage.DownloadInputStream(file.FileID);
                bytes = indata.readAllBytes();
                this.file_cache.put(pathFull,bytes);
            }else {
                bytes = this.file_cache.get(pathFull);
            }
            int length = bytes.length;
            int bytesToRead = (int) Math.min(length - offset, size);
            byte[] slice = Arrays.copyOfRange(bytes, (int) offset, (int) offset+bytesToRead);
            synchronized (this) {
                buf.put(0, slice, 0, bytesToRead);

            }
            return bytesToRead;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -ErrorCodes.ENOENT();
        }


    }


    public static void main(String[] args) throws SQLException {
        if(!new File("/home/alles/Files.mv.db").exists()){
            new H2Create().createTable();
            H2Insert.insertRecord("test.txt","/","/test.txt","BQACAgEAAxkDAAICYmFItJzNnSqqAmJaAlwBWUVo58G7AAJNAgACnsdIRs3t3Nn1PHisIAQ", "25");
            H2Insert.insertRecord("mumei.jpg","/","/mumei.jpg","BQACAgEAAxkDAAICYWFBGpMU35zp75S0flXscMTovGxZAAJaAQACIskJRkuh5Iv98MIaIAQ", "86101");
            H2Insert.insertRecord("sing.webm","/","/sing.webm","BQACAgEAAxkDAAICdGFI2pfvFyUbkpJ-E9BUFKp_0SKSAAKCAgACnsdIRtXy5sidiFDeIAQ.BQACAgEAAxkDAAICc2FI2pK8etPmorhkkEXUwG_il4dfAAKBAgACnsdIRrqyPqSeXgVRIAQ", "28907884");
            H2Insert.insertRecord("apk.apk","/","/apk.apk","BQACAgEAAxkDAAICdWFI5n4Hwqxnm-2fMMbkNKNXdwN-AAKKAgACnsdIRo-DpA6VtoFRIAQ", "17179384");

        }
        TelegramStorage storage = new TelegramStorage("1226512047","1470338630:AAHqadAzxn5dMPV47rKsTYD2u_zORmzIBrU");

        Main stub = new Main();
        stub.storage = storage;
        try {
            String path;
            switch (Platform.getNativePlatform().getOS()) {
                case WINDOWS:
                    path = "J:\\";
                    break;
                default:
                    path = "/home/alles/mount/";
            }
            stub.mount(Paths.get(path), true, false);
        } finally {
            stub.umount();
        }
    }
}