package com.proxiad.merelles.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.proxiad.merelles.game.Board;
import com.proxiad.merelles.game.Command;
import com.proxiad.merelles.game.Location;
import com.proxiad.merelles.game.Piece;
import com.proxiad.merelles.game.PlayerColor;

public class ParserTests {

	private static final String NO_COMMAND = "No command";

	private static final String SHOULD_THROW_AN_EXCEPTION = "Should throw an exception";

	@Mock
	private Board board;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testMovePiece1() throws ParsingException {
		// MOVE_PIECE_ID TO_A TO_R REMOVE_PIECE_ID TEXT
		String textFromPlayer = "1 3 2 0 ; Foobar";
		Parser parser = new Parser();
		Location previousLocation = new Location(2, 2);
		when(board.findPieceById(1)).thenReturn(new Piece(1, PlayerColor.BLACK, previousLocation));
		when(board.findPieceById(0)).thenReturn(null);
		Command command = parser.parse(textFromPlayer, board);
		assertEquals(1, command.getMovedPiece().getId());
	}

	@Test
	public void testTargetLocation() throws ParsingException {
		String textFromPlayer = "1 3 2 0 ; Foobar";
		Parser parser = new Parser();
		Location previousLocation = new Location(2, 2);
		when(board.findPieceById(1)).thenReturn(new Piece(1, PlayerColor.BLACK, previousLocation));
		when(board.findPieceById(0)).thenReturn(null);
		Command command = parser.parse(textFromPlayer, board);
		Location expectedLocation = new Location(3, 2);
		assertEquals(expectedLocation, command.getTargetLocation());
	}

	@Test
	public void testOtherTargetLocation() throws ParsingException {
		String textFromPlayer = "1 7 1 0 ; Foobar";
		Parser parser = new Parser();
		Location previousLocation = new Location(7, 0);
		when(board.findPieceById(1)).thenReturn(new Piece(1, PlayerColor.BLACK, previousLocation));
		when(board.findPieceById(0)).thenReturn(null);
		Command command = parser.parse(textFromPlayer, board);
		Location expectedLocation = new Location(7, 1);
		assertEquals(expectedLocation, command.getTargetLocation());
	}

	@Test
	public void testMovePiece2() throws ParsingException {
		// MOVE_PIECE_ID TO_A TO_R REMOVE_PIECE_ID ; TEXT
		String textFromPlayer = "2 3 2 0 ; Foobar";
		Parser parser = new Parser();
		when(board.findPieceById(2)).thenReturn(new Piece(2, PlayerColor.BLACK, new Location(3, 2)));
		when(board.findPieceById(0)).thenReturn(null);
		Command command = parser.parse(textFromPlayer, board);
		assertEquals(2, command.getMovedPiece().getId());
	}

	@Test
	public void testRemovePiece() throws ParsingException {
		// MOVE_PIECE_ID TO_A TO_R REMOVE_PIECE_ID ; TEXT
		String textFromPlayer = "2 3 2 1 ; Foobar";
		Parser parser = new Parser();
		Piece removePiece = new Piece(2, PlayerColor.BLACK, new Location(3, 1));
		when(board.findPieceById(2)).thenReturn(new Piece(2, PlayerColor.BLACK, new Location(3, 2)));
		when(board.findPieceById(1)).thenReturn(removePiece);
		Command command = parser.parse(textFromPlayer, board);
		assertSame(removePiece, command.getRemovePiece());
	}

	@Test
	public void testNoRemovePiece() throws ParsingException {
		// MOVE_PIECE_ID TO_A TO_R REMOVE_PIECE_ID ; TEXT
		String textFromPlayer = "2 3 2 0 ; Foobar";
		Parser parser = new Parser();
		when(board.findPieceById(2)).thenReturn(new Piece(2, PlayerColor.BLACK, new Location(3, 2)));
		when(board.findPieceById(0)).thenReturn(null);
		Command command = parser.parse(textFromPlayer, board);
		assertEquals(2, command.getMovedPiece().getId());
	}

	@Test
	public void testSillyCommand() {
		String textFromPlayer = "You won't understand that";
		Parser parser = new Parser();
		ParsingException exception = null;
		try {
			parser.parse(textFromPlayer, board);
			fail(SHOULD_THROW_AN_EXCEPTION);
		} catch(ParsingException exc) {
			exception = exc;
		}
		assertEquals("Expecting number for PIECE_ID but found 'You'", exception.getMessage());
	}

	@Test
	public void testEmptyCommand() {
		String textFromPlayer = "";
		Parser parser = new Parser();
		ParsingException exception = null;
		try {
			parser.parse(textFromPlayer, board);
			fail(SHOULD_THROW_AN_EXCEPTION);
		} catch(ParsingException exc) {
			exception = exc;
		}
		assertEquals(NO_COMMAND, exception.getMessage());
	}

	@Test
	public void testPartialCommand() {
		String textFromPlayer = "2 3 2";
		Parser parser = new Parser();
		ParsingException exception = null;
		try {
			parser.parse(textFromPlayer, board);
			fail(SHOULD_THROW_AN_EXCEPTION);
		} catch(ParsingException exc) {
			exception = exc;
		}
		assertEquals("Expecting REMOVE_PIECE_ID but found end of line", exception.getMessage());
	}

	@Test
	public void testNullCommand() {
		String textFromPlayer = null;
		Parser parser = new Parser();
		ParsingException exception = null;
		try {
			parser.parse(textFromPlayer, board);
			fail(SHOULD_THROW_AN_EXCEPTION);
		} catch(ParsingException exc) {
			exception = exc;
		}
		assertEquals(NO_COMMAND, exception.getMessage());
	}

	@Test
	public void testMoveUnknownPiece() throws ParsingException {
		// MOVE_PIECE_ID TO_A TO_R REMOVE_PIECE_ID TEXT
		String textFromPlayer = "25 3 2 0 Foobar";
		Parser parser = new Parser();
		when(board.findPieceById(25)).thenReturn(null);
		Command command = parser.parse(textFromPlayer, board);
		assertNull(command.getMovedPiece());
	}

	@Test
	public void testMovePieceYielding() throws ParsingException {
		// MOVE_PIECE_ID TO_A TO_R REMOVE_PIECE_ID TEXT
		String textFromPlayer = "1 3 2 0 ; Foobar";
		Parser parser = new Parser();
		Location previousLocation = new Location(2, 2);
		when(board.findPieceById(1)).thenReturn(new Piece(1, PlayerColor.BLACK, previousLocation));
		when(board.findPieceById(0)).thenReturn(null);
		Command command = parser.parse(textFromPlayer, board);
		assertEquals("Foobar", command.getMessage());
	}

	@Test
	public void testMovePieceSilent() throws ParsingException {
		// MOVE_PIECE_ID TO_A TO_R REMOVE_PIECE_ID TEXT
		String textFromPlayer = "1 3 2 0 ; ";
		Parser parser = new Parser();
		Location previousLocation = new Location(2, 2);
		when(board.findPieceById(1)).thenReturn(new Piece(1, PlayerColor.BLACK, previousLocation));
		when(board.findPieceById(0)).thenReturn(null);
		Command command = parser.parse(textFromPlayer, board);
		assertEquals("", command.getMessage());
	}

	@Test
	public void testMovePieceVoid() throws ParsingException {
		// MOVE_PIECE_ID TO_A TO_R REMOVE_PIECE_ID TEXT
		String textFromPlayer = "1 3 2 0";
		Parser parser = new Parser();
		Location previousLocation = new Location(2, 2);
		when(board.findPieceById(1)).thenReturn(new Piece(1, PlayerColor.BLACK, previousLocation));
		when(board.findPieceById(0)).thenReturn(null);
		Command command = parser.parse(textFromPlayer, board);
		assertEquals("", command.getMessage());
	}
}
