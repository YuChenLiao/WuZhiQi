package sample;

import java.lang.Integer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    // 绘制棋盘的数据
    private Integer chessPaneWidth = 570;// 780
    private Integer chessPaneHeight = 570;
    private Integer chessPaneUnitWidth = 30;
    private Integer chessPaneUnitHeight = 30;
    private Integer startPosition = 30;

    private AnchorPane anchorPane;
    private Boolean  isBlack = true;
    private Boolean  isSuccess = false;
    // 储存对局信息的数组
    private Integer[][]  data = new Integer[19][19];//[13][13]

    private String chessPaneLineColor = "#000000";
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        initData();

        this.anchorPane = new AnchorPane();
        Canvas canvas = new Canvas(chessPaneWidth,chessPaneHeight);
        anchorPane.getChildren().add(canvas);

        this.drawChessPane(canvas);

        Scene scene = new Scene(anchorPane);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                calculateMouseClickPosition(event.getSceneX(),event.getSceneY() );
            }
        });

        Button button = new Button("重新开始");
        button.setLayoutY(590);
        button.setLayoutX(265);
        anchorPane.getChildren().add(button);
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                isBlack = true;
                isSuccess = false;
                data = new Integer[19][19];
                initData();
                anchorPane.getChildren().removeAll(anchorPane.getChildren());
                anchorPane.getChildren().add(canvas);
                anchorPane.getChildren().add(button);

            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("javafx五子棋");
        primaryStage.setWidth(630);// 840
        primaryStage.setHeight(680);// 860
        primaryStage.setResizable(false);
        primaryStage.show();


    }

    //初始化存储数据
    public void initData(){

        for ( int i=0; i<19; i++ ){
            for( int j=0; j<19; j++ ){
                data[i][j] = 0;
            }
        }
    }

    //存储数据
    public void saveData( Integer i, Integer j, Boolean isBlack ){

        i= i-1;j=j-1;
        if ( 0 <= i && i < 19  && j>=0 && j<19 ){
            data[i][j] = isBlack ? 1: 2;
        }
    }
    //展示胜利的弹框
    public void showSuccess( Stage primaryStage, String text ){

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(hBox);

        Label label = new Label();
        label.setText(text);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(Font.font(20));
        label.setTextFill(Paint.valueOf("#333"));
        hBox.getChildren().add(label);

        stage.setScene(scene);
        stage.setWidth(300);
        stage.setHeight(200);
        //获取primaryStage的屏幕位置
        primaryStage.getX();
        primaryStage.getY();
        stage.setX( primaryStage.getX()+primaryStage.getWidth()/2-stage.getWidth()/2 );
        stage.setY( primaryStage.getY()+primaryStage.getHeight()/2-stage.getHeight()/2 );
        stage.show();

    }
    //检测鼠标点击位置
    public void calculateMouseClickPosition( double x, double y ){
        if ( isSuccess ){
            return;
        }
        //有效点击位置计算
        Integer availableMinX = chessPaneUnitWidth/2;
        Integer availableMinY = chessPaneUnitHeight/2;
        Integer availableMaxX = chessPaneWidth + chessPaneUnitWidth/2;
        Integer availabelMaxY = chessPaneHeight + chessPaneUnitHeight/2;

        //判断是否在有效位置上
        if ( availableMinX <= x && x <= availableMaxX && availableMinY <= y && y <= availabelMaxY ){

            //计算出其所在行，所在列
            double translateX = x -15;
            double translateY = y -15;
            Integer i =  (int)Math.ceil(translateX/30+0.000001);
            System.out.println("X:"+i);
            Integer j =  (int)Math.ceil(translateY/30+0.000001);
            System.out.println("Y:"+j);


            //存储位置
            isBlack = !isBlack;
            if ( isBlack ){
                drawBlackChessUnit(anchorPane, i, j);
            }else {
                drawWhiteChessUnit(anchorPane, i, j);
            }

            //数组是从0开始的
            saveData( i, j, isBlack );
            calculateIsFinish( i,j, isBlack );

        }else {

            System.out.println("无效点击位置");
        }

    }
    //计算是否胜利
    public void calculateIsFinish( Integer xi, Integer yj, Boolean isBlack ){
        xi = xi-1; yj = yj-1;
        //如果是黑子，那么存储值是1；
        //如果是白子，那么存储值是2；
        Integer saveValue = isBlack? 1: 2;
        //根据最后一个点的落点位置，计算八个方位是否有呈5个一致的棋子。

        Integer[][] validateData = new Integer[4][9];
        for ( int i=0; i<4 ; i++ ){
            for ( int j=0; j<9; j++ ){
                validateData[i][j] = 0;
            }
        }

        //获取竖向
        int k=0;
        for( int i=yj-4; i<yj+5; i++ ){

            if ( i>=0 && i<19 ){

                validateData[0][k] = data[xi][i];
            }
            k++;
        }

        Integer leftCount = 0;
        Boolean isLeftCount = true;
        Integer rightCount = 0;
        Boolean isRightCount = true;
        for ( int i = 4; i>=0; i-- ){

            //分别从左，右两边试探
            if ( validateData[0][i] == saveValue && isLeftCount ){
                leftCount ++;
            }else {
                isLeftCount = false;
            }
            if( validateData[0][9-i-1] == saveValue && isRightCount ){
                rightCount ++;
            }else{
                isRightCount = false;
            }

            if ( leftCount + rightCount >= 6 ){

                isSuccess = true;
                showSuccess(primaryStage, (isBlack?"黑色":"白色") + "获得胜利");
            }
        }

        //获取横向
        k=0;
        leftCount = 0;
        rightCount = 0;
        isLeftCount = true;
        isRightCount = true;
        for ( int i=xi-4; i<xi+5; i++  ){
            if ( i>=0 && i < 19 ){

                validateData[1][k] = data[i][yj];
            }
            k++;
        }

        for ( int i = 4; i>=0; i-- ){

            //分别从左，右两边试探
            if ( validateData[1][i] == saveValue && isLeftCount ){
                leftCount ++;
            }else {
                isLeftCount = false;
            }

            if( validateData[1][9-i-1] == saveValue && isRightCount ){
                rightCount ++;
            }else{
                isRightCount = false;
            }

            if ( leftCount + rightCount >= 6 ){

                isSuccess = true;
                showSuccess(primaryStage, (isBlack?"黑色":"白色") + "获得胜利");
            }
        }

        //获取左斜线
        k=0;
        leftCount = 0;
        rightCount = 0;
        isLeftCount = true;
        isRightCount = true;
        for ( int i=xi-4,j=yj-4; i<xi+5 && j<yj+5; i++,j++  ) {

            //i表示列的index， j表示行的index
            if ( i>=0 && i<19 && j>=0 && j<19 ){
                validateData[2][k] = data[i][j];
            }
            k++;

        }
        for ( int i = 4; i>=0; i-- ){
            //分别从左，右两边试探
            if ( validateData[2][i] == saveValue && isLeftCount ){
                leftCount ++;
            }else {
                isLeftCount = false;
            }

            if( validateData[2][9-i-1] == saveValue && isRightCount ){
                rightCount ++;
            }else{
                isRightCount = false;
            }

            if ( leftCount + rightCount >= 6 ){

                isSuccess = true;
                showSuccess(primaryStage, (isBlack?"黑色":"白色") + "获得胜利");
            }
        }

        //获取右斜线
        k=0;
        leftCount = 0;
        rightCount = 0;
        isLeftCount = true;
        isRightCount = true;
        for ( int i=xi-4,j=yj+4; i<xi+5 && j>yj-5; i++,j--  ) {

            //i表示列的index， j表示行的index
            if ( i>=0 && i<19 && j>=0 && j<19 ){
                validateData[3][k] = data[i][j];
            }
            k++;

        }

        for ( int i = 4; i>=0; i-- ){

            //分别从左，右两边试探
            if ( validateData[3][i] == saveValue && isLeftCount ){
                leftCount ++;
            }else {
                isLeftCount = false;
            }

            if( validateData[3][9-i-1] == saveValue && isRightCount ){
                rightCount ++;
            }else{
                isRightCount = false;
            }

            if ( leftCount + rightCount >= 6 ){

                isSuccess = true;
                showSuccess(primaryStage, (isBlack?"黑色":"白色") + "获得胜利");
            }
        }
    }
    // 绘制白棋
    public void drawWhiteChessUnit( AnchorPane anchorPane, Integer i, Integer j ){

        Button button =  new Button();
        anchorPane.getChildren().add(button);
        button.setStyle("-fx-background-radius: 30; -fx-effect:dropshadow(three-pass-box, #72b9da, 8.0,0, 0, 0); ");
        drawChessUnit(button, i, j);
    }
    // 绘制黑棋
    public void drawBlackChessUnit( AnchorPane anchorPane, Integer i, Integer j ){

        Button button =  new Button();
        anchorPane.getChildren().add(button);
        button.setStyle("-fx-background-color: #000;-fx-background-radius: 30; -fx-effect:dropshadow(three-pass-box, #72b9da, 8.0,0, 0, 0);  ");
        drawChessUnit(button, i, j);
    }
    // 绘制棋子位置
    public void drawChessUnit( Button button, Integer i, Integer j ) {

        Integer width = chessPaneUnitWidth;
        button.setFocusTraversable(false);
        button.setPrefSize(width, width);
        button.setLayoutX(i*chessPaneUnitWidth-width/2);
        button.setLayoutY(j*chessPaneUnitWidth-width/2);
    }
    // 绘制棋盘
    public void drawChessPane( Canvas canvas ){

        GraphicsContext gc = canvas.getGraphicsContext2D();

        for ( int i=0; i<19; i++ ){
            gc.setStroke(Paint.valueOf(chessPaneLineColor));
            gc.setLineWidth(1);
            gc.strokeLine(startPosition, (i+1)*chessPaneUnitHeight, chessPaneWidth,(i+1)*chessPaneUnitHeight);
            gc.strokeLine((i+1)*chessPaneUnitWidth, startPosition, (i+1)*chessPaneUnitWidth,chessPaneHeight);
        }
    }

    public static void main(String[] args) {
    // 主方法设置
        launch(Main.class,args);
    }
}
