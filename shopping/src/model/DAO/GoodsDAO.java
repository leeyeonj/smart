package model.DAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.DTO.CartDTO;
import model.DTO.OrderList;
import model.DTO.PaymentDTO;
import model.DTO.ProdReviewDTO;
import model.DTO.ProductCartDTO;
import model.DTO.ProductDTO;
import model.DTO.ProductReviewDTO;
import model.DTO.PurchaseDTO;

public class GoodsDAO extends DataBaseInfo{
	final String COLUMNS = "PROD_NUM, PROD_NAME, PROD_PRICE,"
			+ "PROD_IMAGE, PROD_DETAIL,PROD_CAPACITY,PRUD_SUPPLYER,"
			+ "PROD_DEL_FEE,RECOMMEND, EMPLOYEE_ID,CTGR ";
	
	public List<ProdReviewDTO> prodReviewSelect(String prodNum) {
		List<ProdReviewDTO> list = new ArrayList<ProdReviewDTO>();
		sql = " select rpad(substr(p.mem_id,1,3),length(p.mem_id),'*') mem_Id, "
				+ " review_content, review_img, review_date "
				+ " from purchase p , review r "
				+ "	where p.purchase_num = r.purchase_num" 
				+ " and r.prod_num = ?";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prodNum);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ProdReviewDTO dto = new ProdReviewDTO();
				dto.setMemId(rs.getString(1));
				dto.setReviewContent(rs.getString(2));
				dto.setReviewDate(rs.getDate(4));
				dto.setReviewImg(rs.getString(3));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return list;
	}
	
	public void reviewUpdate(ProductReviewDTO dto) {
		sql = " update review "
				+ " set REVIEW_CONTENT = ? "
				+ " where PURCHASE_NUM = ? and PROD_NUM = ? ";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getReviewContent());
			pstmt.setString(2, dto.getPurchaseNum());
			pstmt.setString(3, dto.getProdNum());
			int i = pstmt.executeUpdate();
			System.out.println(i + "개가 수정되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
	}
	
	public void reviewSelect(ProductReviewDTO dto) {
		sql = " select PURCHASE_NUM, PROD_NUM, REVIEW_DATE,"
				+ " REVIEW_CONTENT, REVIEW_IMG "
				+ " from review "
				+ " where PURCHASE_NUM = ? and PROD_NUM = ? ";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getPurchaseNum());
			pstmt.setString(2, dto.getProdNum());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				dto.setProdNum(rs.getString("prod_Num"));
				dto.setPurchaseNum(rs.getString("purchase_Num"));
				dto.setReviewContent(rs.getString("review_Content"));
				dto.setReviewDate(rs.getString("review_Date"));
				dto.setReviewImg(rs.getString("review_Img"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
	}
	
	public void reviewInsert(ProductReviewDTO dto) {
		sql = " insert into review(PURCHASE_NUM, PROD_NUM, REVIEW_DATE,"
				+ " REVIEW_CONTENT, REVIEW_IMG ) "
				+ " values(?,?,sysdate,?,?)";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getPurchaseNum());
			pstmt.setString(2, dto.getProdNum());
			pstmt.setString(3, dto.getReviewContent());
			pstmt.setString(4, dto.getReviewImg());
			int i = pstmt.executeUpdate();
			System.out.println(i + "개가 입력되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
	}
	public void payment(PaymentDTO dto) {
		String num = " select to_char(sysdate,'yyyymmdd') || " 
	               +"       nvl2(max(PAYMENT_APPR_NUM),"
	               +"      substr(max(PAYMENT_APPR_NUM),-6),100000) + 1 " 
	               +" from payment " 
	               +" where substr(PAYMENT_APPR_NUM, 1, 8)"
	               +" = to_char(sysdate,'yyyymmdd')";
		
		sql = " insert into payment (PURCHASE_NUM,MEM_ID,PAYMENT_METHOD,"
				+ "                  PAYMENT_APPR_PRICE,PAYMENT_APPR_NUM,"
				+ "					 PAYMENT_APPR_DATE, PAYMENT_NUMBER ) "
			+ " values (?, ?, ?, ?, ( "+ num +" ), sysdate, ?  )"; 
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getPurchaseNum());
			pstmt.setString(2, dto.getMemId());
			pstmt.setString(3, dto.getPaymentMethod());
			pstmt.setString(4, dto.getPaymentApprPrice());
			pstmt.setString(5, dto.getPaymentNumber());
			int i = pstmt.executeUpdate();
			System.out.println(i + "개가 저장되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
	}
	public List<OrderList> orderList(String memId){
		List<OrderList> list = new ArrayList<OrderList>();
		sql = " select p2.PURCHASE_DATE, p4.PAYMENT_APPR_NUM , p1.prod_num, "
				 +"     p2.PURCHASE_NUM, p1.prod_name, p1.PRUD_SUPPLYER," 
				 +"     p2.PURCHASE_TOT_PRICE, p1.prod_image ,review_content"
				 +" from products p1, purchase p2, purchase_list p3, payment p4, review r"
				 +" where p3.prod_num = p1.prod_num " 
				 +" and p3.PURCHASE_NUM = p2.PURCHASE_NUM "
				 +" and p3.PURCHASE_NUM = r.PURCHASE_NUM(+)" 
				 +" and p3.prod_num = r.prod_num(+)"
				 +" and p2.PURCHASE_NUM = p4.PURCHASE_NUM(+)" 
				 +" and p2.mem_id = ? " 
				 +" order by PURCHASE_NUM desc";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memId);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				OrderList dto = new OrderList();
				dto.setPaymentApprNum(rs.getString("payment_Appr_Num"));
				dto.setProdImage(rs.getString("prod_Image"));
				dto.setProdName(rs.getString("prod_Name"));
				dto.setProdNum(rs.getString("prod_Num"));
				dto.setProdSupplyer(rs.getString("prud_Supplyer"));
				dto.setPurchaseDate(rs.getString("purchase_Date"));
				dto.setPurchaseTotPrice(rs.getString("purchase_Tot_Price"));
				dto.setPurchaseNum(rs.getString("purchase_Num"));
				dto.setReviewContent(rs.getString("review_Content"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return list;
	}
	
	public void cartDel(String prodNum,	String memId) {
		sql = " delete from cart "
			+ " where mem_id = ? and prod_num = ?";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memId);
			pstmt.setString(2, prodNum);
			int i = pstmt.executeUpdate();
			System.out.println(i + "개가 삭제되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
	}
	public void purchaseListInsert(String purchaseNum,String prodNum,
			String memId) {
		sql = " insert into purchase_list(PURCHASE_NUM, PROD_NUM,"
			+ " PURCHASE_QTY, PURCHASE_PRICE ) "
			+ " select ?, PROD_NUM, CART_QTY, CART_PRICE "
			+ " from cart "
			+ " where PROD_NUM = ? and mem_id = ? ";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, purchaseNum);
			pstmt.setString(2, prodNum);
			pstmt.setString(3, memId);
			int i = pstmt.executeUpdate();
			System.out.println(i + "개가 입력되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void purchaseInsert(PurchaseDTO dto) {
		sql = " insert into purchase (PURCHASE_NUM,MEM_ID,"
			+ " PURCHASE_TOT_PRICE, PURCHASE_ADDR, PURCHASE_METHOD,"
			+ " PURCAHSE_REQUEST,RECEIVER_NAME,RECEIVER_PHONE,"
			+ " PURCHASE_DATE) "
			+ " values(?,?,?,?,?,?,?,?,sysdate)";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getPurchaseNum());
			pstmt.setString(2, dto.getMemId());
			pstmt.setString(3, dto.getPurchaseTotPrice());
			pstmt.setString(4, dto.getPurchaseAddr());
			pstmt.setString(5, dto.getPurchaseMethod());
			pstmt.setString(6, dto.getPurchaseRequest());
			pstmt.setString(7, dto.getReceiverName());
			pstmt.setString(8, dto.getReceiverPhone());
			int i = pstmt.executeUpdate();
			System.out.println(i + "개가 입력되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
	}
	
	public void prodDel(String prodNum) {
		sql = " delete from products "
				+ " where PROD_NUM = ? ";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prodNum);
			int i = pstmt.executeUpdate();
			System.out.println(i + "가 삭제되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
	}
	public ProductCartDTO prodCart(String prodNum, String memId) {
		ProductCartDTO dto = null;
		sql = "select p.PROD_NUM , PROD_NAME, PROD_PRICE, "
				+ "	  PRUD_SUPPLYER, PROD_DEL_FEE, prod_image,"
				+ "       MEM_ID, CART_QTY, CART_PRICE" 
				+ " from products p, cart c" 
				+ " where p.PROD_NUM = c.PROD_NUM "
				+ " and MEM_ID = ? and c.PROD_NUM = ?"; 
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memId);
			pstmt.setString(2, prodNum);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				dto = new ProductCartDTO();
				dto.setCartDTO(new CartDTO());
				dto.setProductDTO(new ProductDTO());
				dto.getProductDTO().setProdNum(rs.getString("prod_num"));
				dto.getCartDTO().setCartPrice(rs.getInt("CART_PRICE"));
				dto.getCartDTO().setCartQty(rs.getString("cart_Qty"));
				dto.getProductDTO()
				   .setProdDelFee(rs.getString("prod_Del_Fee"));
				dto.getProductDTO()
					.setProdImage(rs.getString("prod_Image"));
				dto.getProductDTO()
				   .setProdName(rs.getString("prod_Name"));
				dto.getProductDTO()
				   .setProdPrice(rs.getInt("prod_Price"));
				dto.getProductDTO()
				   .setProdSupplyer(rs.getString("prud_Supplyer"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return dto;
	}
	public void cartProdDel(CartDTO dto) {
		sql = " delete from cart "
			+ " where MEM_ID = ? and PROD_NUM = ? ";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getMemId());
			pstmt.setString(2, dto.getProdNum());
			int i = pstmt.executeUpdate();
			System.out.println(i + "개가 삭제되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
	}
	public void cartQtyDown(CartDTO dto) {//0621
		sql = " update cart "
				+ " set CART_QTY = CART_QTY - ?,"
				+ " CART_PRICE = CART_PRICE - ? "
				+ " where MEM_ID = ? and PROD_NUM = ? ";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, 1);
			pstmt.setInt(2, dto.getCartPrice());
			pstmt.setString(3, dto.getMemId());
			pstmt.setString(4, dto.getProdNum());
			int i = pstmt.executeUpdate();
			System.out.println(i + "개가 수정되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
	}
	
	public List cartList(String memId) {
		List list = new ArrayList();
		sql = "select p.PROD_NUM , PRUD_SUPPLYER, PROD_DEL_FEE,"
				+ "  PROD_IMAGE, PROD_NAME ,PROD_PRICE," 
				+ "        CART_PRICE, CART_QTY" 
				+ " from products p, cart c" 
				+ " where p.PROD_NUM = c.prod_num and c.mem_id = ?";
			getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memId);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ProductCartDTO dto = new ProductCartDTO();
				dto.setCartDTO(new CartDTO());
				dto.setProductDTO(new ProductDTO());
				dto.getProductDTO().setProdNum(rs.getString("prod_num"));
				dto.getCartDTO().setCartPrice(rs.getInt("CART_PRICE"));
				dto.getCartDTO().setCartQty(rs.getString("cart_Qty"));
				dto.getProductDTO().setProdDelFee(rs.getString("prod_Del_Fee"));
				dto.getProductDTO().setProdImage(rs.getString("prod_Image"));
				dto.getProductDTO().setProdName(rs.getString("prod_Name"));
				dto.getProductDTO().setProdPrice(rs.getInt("prod_Price"));
				dto.getProductDTO().setProdSupplyer(rs.getString("prud_Supplyer"));
				list.add(dto);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	//merge 병합 해줌
	public void cartInsert(CartDTO dto) {
		sql = " merge into cart c "
				+ " using (select prod_num "
				+ "        from products where prod_num = ?) p "
				+ " on (c.prod_num = p.prod_num and c.mem_id = ? ) "
				+ " When MATCHED THEN "
				+ " 	update set CART_QTY = CART_QTY + ? ,"
				+ "                CART_PRICE = CART_PRICE + ? "
				+ " When not MATCHED THEN  "
				+ " insert (c.MEM_ID,c.PROD_NUM,c.CART_QTY,"
										+ "c.CART_PRICE) "
				+ " values(?,?,?,?)";
			getConnect();
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, dto.getProdNum());
			pstmt.setString(2, dto.getMemId());
			pstmt.setString(3, dto.getCartQty());
			pstmt.setInt(4, dto.getCartPrice());
			pstmt.setString(5, dto.getMemId());
			pstmt.setString(6, dto.getProdNum());
			pstmt.setString(7, dto.getCartQty());
			pstmt.setInt(8, dto.getCartPrice());
			int i = pstmt.executeUpdate();
			System.out.println(i+"개가 저장되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void goodsUpdate(ProductDTO dto) {
		sql = " update products "
			+ " set PROD_NAME = ? , PROD_PRICE = ?,"
			+ "     PROD_DETAIL = ?, PROD_CAPACITY= ? , "
			+ "     PRUD_SUPPLYER = ? , PROD_DEL_FEE = ?,"
			+ "     RECOMMEND = ?"
			+ " where PROD_NUM = ? ";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(8, dto.getProdNum());
			pstmt.setString(1, dto.getProdName());
			pstmt.setInt(2, dto.getProdPrice());
			pstmt.setString(3, dto.getProdDetail());
			pstmt.setString(4, dto.getProdCapacity());
			pstmt.setString(5, dto.getProdSupplyer());
			pstmt.setString(6, dto.getProdDelFee());
			pstmt.setString(7, dto.getRecommend());
			int i = pstmt.executeUpdate();
			System.out.println(i + "개가 수정되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
	}
	public ProductDTO GoodsOne(String prodNum) {
		ProductDTO dto = null;
		sql = "select " + COLUMNS +", "
			+ " case CTGR when 'wear' then '의류' "
			+ "			  when 'cosmetic' then '화장품' "
			+ "           when 'food' then '음식' "
			+ "			  when 'car' then '자동차용품' " 
			+ "           end CTGR1 "
			+ " from products "
			+ " where PROD_NUM = ?";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prodNum);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				dto = new ProductDTO();
				dto.setCtgr(rs.getString("CTGR1"));
				dto.setEmployeeId(rs.getString("employee_Id"));
				dto.setProdCapacity(rs.getString("prod_Capacity"));
				dto.setProdDelFee(rs.getString("prod_Del_Fee"));
				dto.setProdDetail(rs.getString("prod_Detail"));
				dto.setProdImage(rs.getString("prod_Image"));
				dto.setProdName(rs.getString("prod_Name"));
				dto.setProdNum(rs.getString("prod_Num"));
				dto.setProdPrice(rs.getInt("prod_Price"));
				dto.setProdSupplyer(rs.getString("prud_Supplyer"));
				dto.setRecommend(rs.getString("recommend"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close();
		}
		return dto;
	}
	
	public List<ProductDTO> goodsList() {
		List<ProductDTO> list = new ArrayList<ProductDTO>();
		sql = "select " + COLUMNS +", "
			+ " case CTGR when 'wear' then '의류' "
			+ "			  when 'cosmetic' then '화장품' "
			+ "           when 'food' then '음식' "
			+ "			  when 'car' then '자동차용품' " 
			+ "           end CTGR1 "
			+ " from products";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ProductDTO dto = new ProductDTO();
				dto.setCtgr(rs.getString("CTGR1"));
				dto.setEmployeeId(rs.getString("employee_Id"));
				dto.setProdCapacity(rs.getString("prod_Capacity"));
				dto.setProdDelFee(rs.getString("prod_Del_Fee"));
				dto.setProdDetail(rs.getString("prod_Detail"));
				dto.setProdImage(rs.getString("prod_Image"));
				dto.setProdName(rs.getString("prod_Name"));
				dto.setProdNum(rs.getString("prod_Num"));
				dto.setProdPrice(rs.getInt("prod_Price"));
				dto.setProdSupplyer(rs.getString("prud_Supplyer"));
				dto.setRecommend(rs.getString("recommend"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return list;
	}
	public void prodInsert(ProductDTO dto) {
		sql = "insert into products ( "+ COLUMNS +") "
			+ " values(?,?,?,?,?,?,?,?,?,?,?) ";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getProdNum());
			pstmt.setString(2, dto.getProdName());
			pstmt.setInt(3, dto.getProdPrice());
			pstmt.setString(4, dto.getProdImage());
			pstmt.setString(5, dto.getProdDetail());
			pstmt.setString(6, dto.getProdCapacity());
			pstmt.setString(7, dto.getProdSupplyer());
			pstmt.setString(8, dto.getProdDelFee());
			pstmt.setString(9, dto.getRecommend());
			pstmt.setString(10, dto.getEmployeeId());
			pstmt.setString(11, dto.getCtgr());
			int i = pstmt.executeUpdate();
			System.out.println(i + "개가 입력되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
	}
	public String goodsNum() {
		String prodNum = null;
		sql = " select prod_seq.nextval from dual";
		getConnect();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			rs.next();
			prodNum = rs.getString(1);//1번을 가져오겠다.
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return prodNum;
	}
}
