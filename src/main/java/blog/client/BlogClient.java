package blog.client;

import com.google.protobuf.Empty;
import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BlogClient {

    private static BlogId createBlog(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            BlogId createResponse = stub.createBlog(
                    Blog.newBuilder()
                            .setAuthor("Thopv")
                            .setTitle("New Blog")
                            .setContent("Hello wold")
                            .build()
            );

            System.out.println("Blog create:" + createResponse.getId());
            return createResponse;
        }catch (StatusRuntimeException e) {
            System.out.println("Couldn't create Blog");
            e.printStackTrace();
            return null;
        }
    }

    private static void readBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId id) {
        try {
            Blog readBlog = stub.readBlog(id);

            System.out.println("Blog read: " + readBlog);
        }catch (StatusRuntimeException e) {
            System.out.println("Couldn't read the blog");
            e.printStackTrace();
        }
    }
    private static void updateBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            Blog newBlog = Blog.newBuilder()
                    .setId(blogId.getId())
                    .setAuthor("TruongCato")
                    .setTitle("Update Item")
                    .setContent("Update Content")
                    .build();
            stub.updateBlog(newBlog);
            System.out.println("Blog update: " + newBlog);
        }catch (StatusRuntimeException e) {
            System.out.println("Couldn't update the blog");
            e.printStackTrace();
        }
    }

    private static void listBlogs(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        stub.listBlogs(Empty.getDefaultInstance()).forEachRemaining(System.out::println);
    }

    private static void deleteBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            stub.deleteBlog(blogId);
            System.out.println("Blog deleted: " + blogId.getId());
        }catch (StatusRuntimeException e) {
            System.out.println("Couldn't delete the blog");
            e.printStackTrace();
        }
    }

    private static void run(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);
        BlogId blogId = createBlog(stub);

        if (blogId == null) {
            return;
        }

        readBlog(stub, blogId);
        updateBlog(stub, blogId);
        listBlogs(stub);
        deleteBlog(stub, blogId);
    }

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        run(channel);

        // do something
        System.out.println("Shutting down");
        channel.shutdown();
    }
}
